package com.viettel.hstd.core.dto;

import java.util.ArrayList;
import java.util.List;

public class BaseResponse<T> {
    public int status;
    public String message;
    public List<String> errors;
    public T result;
    public Integer page;
    public Integer size;
    public Long total;

    public static class ResponseBuilder<E> {
        BaseResponse<E> response;

        public ResponseBuilder() {
            this.response = new BaseResponse<>();
        }


        public ResponseBuilder<E> page(Integer page) {
            this.response.page = page;
            return this;
        }

        public ResponseBuilder<E> size(Integer size) {
            this.response.size = size;
            return this;
        }

        public ResponseBuilder<E> total(Long total) {
            this.response.total = total;
            return this;
        }

        public BaseResponse<E> success(E result, String message) {
            this.response.result = result;
            this.response.status = StatusResponse.SUCCESS;
            this.response.message = message;
            this.response.errors = new ArrayList<>();
            return this.response;
        }

        public BaseResponse<E> success(E result) {
            return success(result, "Thành công");
        }

        public BaseResponse<E> failed(List<String> errors, String message) {
            this.response.status = StatusResponse.FAILED;
            this.response.message = message;
            this.response.errors = errors;
            return this.response;
        }

        BaseResponse<E> failed(List<String> errors) {
            return failed(errors, "Thất bại");
        }
    }

}
