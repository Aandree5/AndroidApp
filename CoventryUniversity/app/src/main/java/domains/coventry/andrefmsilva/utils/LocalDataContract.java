package domains.coventry.andrefmsilva.utils;

import android.provider.BaseColumns;

public final class LocalDataContract
{


    // To prevent from accidentally instantiating the contract class, the constructor is private.
    private LocalDataContract()
    {
    }

    /* Inner class that defines the table contents */
    public static class UserData implements BaseColumns
    {
        public static final String TABLE_NAME = "UserData";
        public static final String USERID = "user_id"; // StudentID or TeacherID
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";
        public static final String SHORTNAME = "shortname";
        public static final String FACULTY = "faculty";
        public static final String STUDYSUBJECT = "study_subject"; // Students: Course | Teachers: Module
        public static final String LASTLOGIN = "last_login";

        public static final String SQL_CREATE_TABLE = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s INTEGER, %s TEXT, ",
                TABLE_NAME, _ID, USERID, USERNAME, EMAIL, SHORTNAME, FACULTY, STUDYSUBJECT, LASTLOGIN);
    }

    /* Inner class that defines the table contents */
    public static class ClassesTimetable implements BaseColumns
    {
        public static final String TABLE_NAME = "ClassesTimetable";
        public static final String DATETIME = "datetime";
        public static final String MODULECODE = "module_code";
        public static final String TEACHERS = "teachers";
        public static final String SHORTNAME = "room_code";
    }

    /* Inner class that defines the table contents */
    public static class PersonalTimetable implements BaseColumns
    {
        public static final String TABLE_NAME = "PersonalTimetable";
        public static final String DATETIME = "datetime";
        public static final String MODULECODE = "title";
        public static final String TEACHERS = "description";
    }
}
