package domains.coventry.andrefmsilva.CustomViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


public class ZoomImageView extends android.support.v7.widget.AppCompatImageView
{
    private float minScale = 1.f;
    private float maxScale = 4.f;
    private Matrix imageMatrix = new Matrix();
    /* Used to copy the image matrix before changing it (Keeps the view from updating until end of function), otherwise keeps applying the changes instantly
     * by saving also allow to continue editing the current matrix values, but check them before applying to the image */
    private Matrix savedMatrix = new Matrix();
    private PointF touchStart = new PointF();
    private PointF touchMiddle = new PointF();
    float oldDist = 1f;

    private EventType mode = EventType.NONE;

    private enum EventType
    {
        NONE,
        DRAG,
        ZOOM
    }

    /**
     * Constructor
     *
     * @param context Context to add the view
     * @param attrs   View attributes set in XML
     */
    public ZoomImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    //TODO: Add double click to zoom in and out
    // Visual impared own be using the part of the app, since its just scaling and moving an image to see it better
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            // On first touch event
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(imageMatrix);
                touchStart.set(event.getX(), event.getY());
                mode = EventType.DRAG;
                break;

            //On two fingers down
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = getPinchDistance(event);

                touchMiddle.set((event.getX(0) + event.getX(1)) / 2,
                        (event.getY(0) + event.getY(1)) / 2);

                mode = EventType.ZOOM;

                break;

            // On fingers up
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = EventType.NONE;

                float[] matrixValues = new float[9];
                imageMatrix.getValues(matrixValues);

                // Lock image scale between min and max (when smaller than min, reset the image to the beginning)
                if (matrixValues[Matrix.MSCALE_X] < minScale)
                    centerImageOnView();

                else if (matrixValues[Matrix.MSCALE_X] > maxScale)
                    imageMatrix.postScale(maxScale / matrixValues[Matrix.MSCALE_X], maxScale / matrixValues[Matrix.MSCALE_X], touchMiddle.x, touchMiddle.y);

                break;

            // On fingers move
            case MotionEvent.ACTION_MOVE:
                if (mode == EventType.DRAG)
                {
                    imageMatrix.set(savedMatrix);

                    PointF translate = checkMatrixInsideView(imageMatrix, new PointF(event.getX() - touchStart.x, event.getY() - touchStart.y));

                    if (translate != null)
                        imageMatrix.postTranslate(translate.x, translate.y);

                }
                else if (mode == EventType.ZOOM && event.getPointerCount() == 2)
                {
                    imageMatrix.set(savedMatrix);

                    float scale = getPinchDistance(event) / oldDist;

                    imageMatrix.postScale(scale, scale, touchMiddle.x, touchMiddle.y);

                    checkMatrixInsideView(imageMatrix);
                }
                break;
        }

        setImageMatrix(imageMatrix);

        return true;
    }

    // Used to center image on screen and set the min and max possible scale values
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        super.onWindowFocusChanged(hasWindowFocus);

        centerImageOnView();

        float[] matrixValues = new float[9];
        imageMatrix.getValues(matrixValues);

        minScale = matrixValues[Matrix.MSCALE_X];
        maxScale = minScale * 4;
    }


    /**
     * Get the space between two fingers (Pinch)
     *
     * @param event The touch event to get the finger positions
     * @return The spacing between both fingers (Pinch)
     */
    private float getPinchDistance(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    /**
     * Center the drawable inseide the view
     */
    private void centerImageOnView()
    {
        Drawable drawable = getDrawable();

        if (drawable != null)
        {
            imageMatrix.setRectToRect(new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()),
                    new RectF(0, 0, getWidth(), getHeight()), Matrix.ScaleToFit.CENTER);


            setImageMatrix(imageMatrix);
        }
        else
            Log.e("centerImageOnView", "Couldn't get drawable.");
    }


    /**
     * Check if the given matrix will be inside view, centered and bound to the biggest side, if not, correct the matrix
     *
     * @param matrix Matrix check and fix translate position
     */
    private void checkMatrixInsideView(Matrix matrix)
    {
        PointF translate = checkMatrixInsideView(matrix, new PointF(0, 0));

        if (translate != null)
            matrix.postTranslate(translate.x, translate.y);
    }


    /**
     * Check if the given position for the given matrix will keep it inside view, centered and bound to the biggest side, if not, correct it and return the position
     *
     * @param matrix   Matrix to translate to the new position
     * @param position New position to translate the matrix to
     * @return Position for the matrix to translate to (Corrected to stay inside the view, centered and the biggest side always bound to the screen side)
     */
    private PointF checkMatrixInsideView(Matrix matrix, PointF position)
    {
        Drawable drawable = getDrawable();
        if (drawable == null)
        {
            Log.e("translateMatrix", "Drawable can't be null!");
            return null;
        }

        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);

        RectF imageRect = new RectF(drawable.getBounds());

        // Map image bounds with the given matrix to get the scaled size for width and height
        matrix.mapRect(imageRect);

        /* Check if the left side of the image has passed the left side of the screen
         *  if so, deduct the inverse of the amout that it has passed by (To reach 0)*/
        if (matrixValues[Matrix.MTRANS_X] + position.x >= 0)
            position.x = matrixValues[Matrix.MTRANS_X] * -1;

            /* Check if the right side of the image has passed the right side of the screen
             *  if so, deduct the inverse of the amout that it has passed by and add the screen width minus the image width (To get the image right side)*/
        else if (matrixValues[Matrix.MTRANS_X] + position.x + imageRect.width() <= getWidth())
            position.x = (matrixValues[Matrix.MTRANS_X] * -1) + (getWidth() - imageRect.width());

        // Same as the left side but for the top
        if (matrixValues[Matrix.MTRANS_Y] + position.y >= 0)
            position.y = matrixValues[Matrix.MTRANS_Y] * -1;

            // Same as the right side but for the bottom
        else if (matrixValues[Matrix.MTRANS_Y] + position.y + imageRect.height() <= getHeight())
            position.y = (matrixValues[Matrix.MTRANS_Y] * -1) + (getHeight() - imageRect.height());

        // Check if either side is smaller than the screen and add an offset to center it
        if (imageRect.width() < getWidth())
            position.x += ((getWidth() / 2) - (imageRect.width() / 2));

        if (imageRect.height() < getHeight())
            position.y += ((getHeight() / 2) - (imageRect.height() / 2));

        return position;
    }
}
