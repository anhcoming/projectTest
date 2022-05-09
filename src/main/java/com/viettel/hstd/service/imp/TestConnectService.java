///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.viettel.hstd.service.imp;
//
//import clients.ConnectService;
//import java.util.Arrays;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.springframework.stereotype.Service;
//
///**
// *
// * @author Tuantm30
// */
//public class TestConnectService {
//
//    // Link release
////    private static final String SESSION_URL = "http://10.60.101.245:8888/ServiceMobile_V02/resources/";
////    private static final String APP_URL = "http://voffice.viettel.vn/app-view";
//    // Link test
//    private static final String SESSION_URL = "http://10.60.108.86:8591/ServiceMobile_V02/resources/";
//    private static final String APP_URL = "http://10.60.108.86:8888/app-view/";
//
//    private static final long TIME_OUT = 1800000l;
//    private static final String USER = "293289";
//    private static final String PASS = "Tien@130498";
//    private static final String TICKET = "ticket";
//
////    /**
////     * @param args the command line arguments
////     */
////    public static void main(String[] args) {
////        try {
//////            getListUser();
//////            getFile();
////            addText();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//
//    // Lay danh sach van ban nhan duoc
//    private static void getListDocument() {
//        ConnectService service = new ConnectService(SESSION_URL, APP_URL, TIME_OUT);
//        boolean check = service.login(USER, PASS);
////        boolean check = service.loginSSO(TICKET);
//        if (check) {
//            Map<String, Object> params = new LinkedHashMap<String, Object>();
//            params.put("isSearchMobile", "1"); // Tham so mac dinh khong thay doi
//            params.put("status", "0"); // Tham so mac dinh khong thay doi
//            params.put("type", "1"); // Tham so mac dinh khong thay doi
//
//            params.put("keyword", ""); // Tham so truyen vao
//            params.put("pageSize", "20"); // Tham so truyen vao
//            params.put("startRecord", "0"); // Tham so truyen vao
//            String result = service.getData("DocumentAction.search", params);
//            System.out.println(result);
//        }
//    }
//
//    // Lay file cua van ban da ban hanh
//    private static void getFile() throws Exception {
//        ConnectService service = new ConnectService(SESSION_URL, APP_URL, TIME_OUT);
//        boolean check = service.login(USER, PASS);
////        boolean check = service.loginSSO(TICKET);
//        if (check) {
//            Map<String, Object> params = new LinkedHashMap<String, Object>();
//            params.put("type", "0"); // Tham so mac dinh khong thay doi
//            params.put("isOriginal", "0"); // Tham so mac dinh khong thay doi
//
//            params.put("documentId", "397246"); // Tham so truyen vao
//            params.put("filePath", "/Text/2019/7/9/108197/447d717b-6bde-4445-b4df-605e15378b0a.pdf"); // Tham so truyen vao
//            params.put("storage", "storage8600"); // Tham so truyen vao
//
//            int result = service.downloadFile("Files.DownloadContentFile", params, "D:/file1.pdf");
//            if (result == 1) {
//                System.out.println("OK");
//            }
//        }
//    }
//
//    // Tim kiem nhan vien theo Ma NV / Email / SDT
//    private static void getListUser() {
//        ConnectService service = new ConnectService(SESSION_URL, APP_URL, TIME_OUT);
//        boolean check = service.login(USER, PASS);
////        boolean check = service.loginSSO(TICKET);
//        if (check) {
//            Map<String, Object> params = new LinkedHashMap<String, Object>();
//            params.put("keyword", "tuandn5");
//            params.put("pageSize", "10");
//            params.put("startRecord", "0");
//
//            String info = service.getData("staffAction.getListUser", params);
//            if (info != null) {
//                System.out.println(info);
//            }
//        }
//    }
//
//    // Lay danh sach vai tro theo don vi cua nhan vien de chon don vi ky cho nhan vien
//    private static void getLitsUserSignWithRole() {
//        ConnectService service = new ConnectService(SESSION_URL, APP_URL, TIME_OUT);
//        boolean check = service.login(USER, PASS);
////        boolean check = service.loginSSO(TICKET);
//        if (check) {
//            List<Long> staffIds = Arrays.asList(new Long[]{434004L});
//            Map<String, Object> params = new LinkedHashMap<String, Object>();
//            params.put("lstStaff", getJSONArrayLong(staffIds));
//
//            String info = service.getData("DocumentService.getLitsUserSignWithRole", params);
//            if (info != null) {
//                System.out.println(info);
//            }
//        }
//    }
//
//    private static JSONArray getJSONArrayLong(List<Long> listLong) {
//        JSONArray array = new JSONArray();
//        try {
//            if (listLong != null && listLong.size() > 0) {
//                JSONObject json;
//                for (Long obj : listLong) {
//                    json = new JSONObject();
//                    json.put("staffId", obj);
//                    array.put(json);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return array;
//    }
//
//    private static void getImageSign() {
//        try {
//            ConnectService service = new ConnectService(SESSION_URL, APP_URL, TIME_OUT);
//            boolean check = service.login(USER, PASS);
////            boolean check = service.loginSSO(TICKET);
//            if (check) {
//                Map<String, Object> params = new LinkedHashMap<String, Object>();
//                params.put("isRequestToSignText", "1");
//                params.put("signStaffIdV2", "434004");
//                params.put("signStaffCode", "158110");
//                params.put("textCreatedDate", "08/08/2019"); // ngay tao van ban trinh ky
//
//                String data = service.getData("imageSignAction.search", params);
//                if (data != null) {
//                    System.out.println(data);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void addText() {
//        try {
//            ConnectService service = new ConnectService(SESSION_URL, APP_URL, TIME_OUT);
//            boolean check = service.login(USER, PASS);
////            boolean check = service.loginSSO(TICKET);
//            if (check) {
//                JSONObject jsonText = new JSONObject();
//                // Begin - Thong tin chinh cua van ban trinh ky
//                jsonText.put("typeId", "47"); // id hinh thuc van ban
//                jsonText.put("sTypeId", "1"); // id do mat van ban: 1-binh thuong, 2-mat
//                jsonText.put("priorityId", "1"); // id do khan van ban: 1-binh thuong, 2-khan, 3-Hoa toc, 4-Thuong khan
//                jsonText.put("officePublishedId", "148844"); // id don vi ban hanh
//                jsonText.put("areaId", "2"); // id linh vuc
//                jsonText.put("code", "XNK-"); // so ky hieu van ban
//                jsonText.put("registerNumber", "001"); // so dang ky
//                jsonText.put("title", "007"); // trich yeu noi dung van ban
//                jsonText.put("description", "007"); // noi dung van ban
//                jsonText.put("autoPromulgateText", "0"); // ban hanh tu dong: 0-ko ban hanh tu dong, 1-ban hanh tu dong
//                jsonText.put("isActive", "1"); // tham so mac dinh
//                // End - Thong tin chinh cua van ban trinh ky
//
//                // Begin - Thong tin file ky chinh
//                JSONArray jsonArrayFileSign = new JSONArray();
//                JSONObject jsonFileSign = new JSONObject();
//                String result = service.uploadFile("/Users/chunamanh/Downloads/011535HopDongThuViec.pdf");
//                jsonFileSign.put("name", "main_file_name.pdf");
//                jsonFileSign.put("filePath", result);
//                jsonFileSign.put("fileOrder", "0");
//                jsonArrayFileSign.put(jsonFileSign);
//                jsonText.put("lstFileSign", jsonArrayFileSign);
//                // End - Thong tin file ky chinh
//
//                // Begin - Thong tin file dinh kem
//                jsonArrayFileSign = new JSONArray();
//                jsonFileSign = new JSONObject();
//                result = service.uploadFile("D:/Documents/Test1.pdf");
//                jsonFileSign.put("name", "other_file_name_01.pdf");
//                jsonFileSign.put("filePath", result);
//                jsonFileSign.put("fileOrder", "0");
//                jsonArrayFileSign.put(jsonFileSign);
//
//                jsonFileSign = new JSONObject();
//                result = service.uploadFile("D:/Documents/Test2.pdf");
//                jsonFileSign.put("name", "other_file_name_02.pdf");
//                jsonFileSign.put("filePath", result);
//                jsonFileSign.put("fileOrder", "1");
//                jsonArrayFileSign.put(jsonFileSign);
//                jsonText.put("listFileSignOther", jsonArrayFileSign);
//                // End - Thong tin file dinh kem
//
//                // Begin - Danh sach nguoi ky
//                JSONArray jsonStaffs = new JSONArray();
//                JSONObject jsonStaff = new JSONObject();
//                jsonStaff.put("staffId", "434004"); // id nhan vien
//                jsonStaff.put("signLevel", "0"); // thu tu ky tu 0,1,2,...
//                jsonStaff.put("signImage", "1"); // danh dau co hien thi anh chu ky khong: 1-hien thi anh chu ky
//                jsonStaff.put("departmentName", "Nhóm dự án - Trung tâm Phần mềm Văn phòng điện tử - Trung tâm Phần mềm Viettel 1"); // name lay tu API getLitsUserSignWithRole (truong: "orgName")
//                jsonStaff.put("departmentSignId", "259224"); // id don vi lay tu api getLitsUserSignWithRole (truong: "orgId")
//                jsonStaff.put("signImageId", "61489"); // id anh chu ky lay tu API getImageSign (truong: "staffImageSignId")
//                jsonStaffs.put(jsonStaff);
//                jsonStaff = new JSONObject();
//                jsonStaff.put("staffId", "900084904"); // id nhan vien
//                jsonStaff.put("signLevel", "1"); // thu tu ky tu 0,1,2,...
//                jsonStaff.put("departmentName", "Thủ trưởng ban giám đốc - Ban Giám đốc Tập đoàn - Khối cơ quan Tập đoàn"); // name lay tu API getLitsUserSignWithRole (truong: "orgName")
//                jsonStaff.put("departmentSignId", "148844"); // id don vi lay tu API getLitsUserSignWithRole (truong: "orgId")
//                jsonStaffs.put(jsonStaff);
//                jsonText.put("lstStaff", jsonStaffs);
//                // End - Danh sach nguoi ky
//
//                String info = service.getData("DocumentService.addText", jsonText.toString());
//                if (info != null) {
//                    System.out.println(info);
//                    Long id = Long.valueOf(info.trim());
//                    sendAndSign(id);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void sendAndSign(Long id) {
//        try {
//            ConnectService service = new ConnectService(SESSION_URL, APP_URL, TIME_OUT);
//            boolean check = service.login(USER, PASS);
////            boolean check = service.loginSSO(TICKET);
//            if (check) {
//                Map<String, Object> params = new LinkedHashMap<String, Object>();
//                params.put("textId", id); // id van ban trinh ky
//                params.put("appCode", "SAP_TD"); // ma ung dung
//                params.put("transCode", "SAP_TD_001"); // ma giao dich
//                String info = service.getData("DocumentService.sendAndSign", params);
//                if (info != null) {
//                    System.out.println(info);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
