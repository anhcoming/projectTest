declare
    SEQ_NEXT_VAL number(10);
    MAX_ID  nvarchar2(255);
    MAX_TABLE_NAME nvarchar2(255);
    TABLE_ID_NAME NVARCHAR2(255);
    IS_SPECIAL_TABLE BOOLEAN := FALSE;
    DIFFERENCE_BETWEEN_MAX_AND_SEQ NUMBER(10) := 0;
begin
    MAX_ID := 0;
    for R in (SELECT * FROM user_tables where TABLE_NAME NOT IN (
                                                                 'EMPLOYEE_VHR_TEMP_BAK',
                                                                 'QRTZ_JOB_DETAILS',
                                                                 'QRTZ_TRIGGERS',
                                                                 'QRTZ_SIMPLE_TRIGGERS',
                                                                 'QRTZ_CRON_TRIGGERS',
                                                                 'QRTZ_SIMPROP_TRIGGERS',
                                                                 'QRTZ_BLOB_TRIGGERS',
                                                                 'QRTZ_CALENDARS',
                                                                 'QRTZ_PAUSED_TRIGGER_GRPS',
                                                                 'QRTZ_FIRED_TRIGGERS',
                                                                 'QRTZ_SCHEDULER_STATE',
                                                                 'QRTZ_LOCKS',
                                                                 'DATABASECHANGELOG',
                                                                 'DATABASECHANGELOGLOCK'

        ))
        loop
            MAX_TABLE_NAME := R.TABLE_NAME;
            IS_SPECIAL_TABLE := R.TABLE_NAME IN ('HISTORY_UPLOAD', 'PROVINCE_AREA');
            IF IS_SPECIAL_TABLE THEN TABLE_ID_NAME := 'ID';
            ELSE TABLE_ID_NAME := R.TABLE_NAME || '_ID';
            END IF;
            DBMS_OUTPUT.PUT_LINE(R.TABLE_NAME);
            EXECUTE IMMEDIATE 'SELECT MAX(' || TABLE_ID_NAME || ') FROM ' || R.TABLE_NAME INTO MAX_ID;
            EXECUTE IMMEDIATE ' SELECT ' || R.TABLE_NAME || '_SEQ.nextval from DUAL' into SEQ_NEXT_VAL;
            DIFFERENCE_BETWEEN_MAX_AND_SEQ := MAX_ID - SEQ_NEXT_VAL;

            IF DIFFERENCE_BETWEEN_MAX_AND_SEQ > 0 THEN
                EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || R.TABLE_NAME || '_SEQ INCREMENT BY ' || DIFFERENCE_BETWEEN_MAX_AND_SEQ;
                EXECUTE IMMEDIATE ' SELECT ' || R.TABLE_NAME || '_SEQ.nextval from DUAL' into SEQ_NEXT_VAL;
                EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || R.TABLE_NAME || '_SEQ INCREMENT BY 1';
            end if;

        END LOOP ;
    DBMS_OUTPUT.PUT_LINE('Highest score = '||MAX_ID||' table = '||MAX_TABLE_NAME);
end;

;
