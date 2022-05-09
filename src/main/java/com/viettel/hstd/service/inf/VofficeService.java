package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.VOfficeSignDTO;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.voffice.ws_autosign.service.FileAttachTranfer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface VofficeService {
    boolean sentVoffice(VOfficeSignDTO request);
    void receiveVoffice();

    String generateDocumentTitle(VOfficeSignDTO request, String defaultName);

    String generateRegisterNumber(VOfficeSignDTO request, SSoResponse sSoResponse);

    List<FileAttachTranfer> addAttachments(List<FileAttachTranfer> listFile, List<String> fileAttachmentPaths);

    // Trả lại filePath
    String writeFile(String fileNameRaw, byte[] data) throws IOException;
}
