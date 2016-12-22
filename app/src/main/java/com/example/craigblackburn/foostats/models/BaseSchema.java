package com.example.craigblackburn.foostats.models;

import android.support.annotation.NonNull;

import com.example.craigblackburn.foostats.FPlayer;


public class BaseSchema {

    protected BaseSchema() {}

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    public static final String END = ";";
    public static final String COMMA = ",";
    public static final String OPEN = "(";
    public static final String CLOSE = ")";
    public static final String TEXT = " TEXT ";
    public static final String INTEGER = " INTEGER ";
    public static final String REQUIRED = " NOT NULL ";
    public static final String UNIQUE = " UNIQUE ";
    public static final String PRIMARY_KEY = " PRIMARY KEY ";
    public static final String FOREIGN_KEY = " FOREIGN KEY ";
    public static final String REFERENCES = " REFERENCES ";
    public static final String CASCADE_ON_UPDATE = " ON DELETE CASCADE ";
    public static final String CASCASE_ON_DELETE = " ON UPDATE CASCADE ";
    public static final String QUOTE = "'";
    public static final String DOT = ".";
    public static final String WHERE = " WHERE ";
    public static final String SELECT = " SELECT ";
    public static final String ALL = " * ";
    public static final String FROM = " FROM ";
    public static final String ON = " ON ";
    public static final String EQUALS = "=";
    public static final String LEFT_JOIN = " LEFT JOIN ";
    public static final String DEFAULT_0 = " DEFAULT 0";
    public static final String SELECT_ALL_FROM = SELECT + ALL + FROM;

    public static final String surroundWithQuotes(String statement) { return QUOTE + statement + QUOTE; }
    public static final String surroundWithParenthesis(String string) { return OPEN + string + CLOSE; }

    public static final String foreignKeyBuilder(String columnName, String referenceTable, String referenceTableId, boolean onCascadeUpdate, boolean onCascadeDelete) {
        String optionCascadeUpdate = "";
        String optionCascadeDelete = "";
        if (onCascadeUpdate) {
            optionCascadeUpdate = CASCADE_ON_UPDATE;
        }

        if (onCascadeDelete) {
            optionCascadeDelete = CASCASE_ON_DELETE;
        }

        return FOREIGN_KEY + OPEN + columnName + CLOSE
                + REFERENCES + referenceTable + "(" + referenceTableId + ")"
                + optionCascadeUpdate + optionCascadeDelete + ";";
    }

    public static String sqlManyToManyQueryBuilder(String table, String joinTable, String table_column_id, String joinTable_column_1, String joinTable_column_2, String searchId) {
        return SELECT_ALL_FROM + table
                + LEFT_JOIN + joinTable
                + ON + table + DOT + table_column_id + EQUALS + joinTable + DOT + joinTable_column_1
                + WHERE + joinTable + DOT + joinTable_column_2 + EQUALS + surroundWithQuotes(searchId) + END;
    }


}
