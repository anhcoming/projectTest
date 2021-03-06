package com.viettel.hstd.dto.hstd;

import java.util.ArrayList;

public class ImportExcelModel<T> {
    private T data;
    private ArrayList<String> errors;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }
}
