package com.atgc.hd.db;

import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Map;


public abstract class DatabaseColumn implements BaseColumns {
    /**
     * The database's name
     */
    public static final String DATABASE_NAME = "train.db";

    /**
     * The version of current database
     */
    public static final int DATABASE_VERSION = 1;
    /**
     * Classes's name extends from this class.
     */
    public static final String USER_ID = "user_id";
    public static final String NAME = "name";
    public static final String TIME = "time";
    public static final String AVATAR = "avatar";

    public static final String[] SUBCLASSES = new String[]{
            "com.atgc.hd.db.EventEntityColumn"
    };

    public String getTableCreateor() {
        return getTableCreator(getTableName(), getTableMap());
    }

    /**
     * Get sub-classes of this class.
     *
     * @return Array of sub-classes.
     */
    public static final Class<DatabaseColumn>[] getSubClasses() {
        ArrayList<Class<DatabaseColumn>> classes = new ArrayList<Class<DatabaseColumn>>();
        Class<DatabaseColumn> subClass = null;
        for (int i = 0; i < SUBCLASSES.length; i++) {
            try {
                subClass = (Class<DatabaseColumn>) Class.forName(SUBCLASSES[i]);
                classes.add(subClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }
        return classes.toArray(new Class[0]);
    }

    /**
     * Create a sentence to create a table by using a hash-map.
     *
     * @param tableName The table's name to create.
     * @param map       A map to store table columns info.
     * @return
     */
    private static final String getTableCreator(String tableName, Map<String, String> map) {
        String[] keys = map.keySet().toArray(new String[0]);
        String value = null;
        StringBuilder creator = new StringBuilder();
        creator.append("CREATE TABLE ").append(tableName).append("( ");
        int length = keys.length;
        for (int i = 0; i < length; i++) {
            value = map.get(keys[i]);
            creator.append(keys[i]).append(" ");
            creator.append(value);
            if (i < length - 1) {
                creator.append(",");
            }
        }
        creator.append(")");
        return creator.toString();
    }

    abstract public String getTableName();

    abstract protected Map<String, String> getTableMap();

}



