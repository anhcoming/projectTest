package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.UploadDTO;
import com.viettel.hstd.entity.hstd.HistoryUploadEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.HistoryUploadFileRepository;
import com.viettel.hstd.service.inf.FileService;
import com.viettel.hstd.util.DateUtils;
import com.viettel.hstd.util.FileUtils;
import com.viettel.hstd.util.VOConfig;
import com.viettel.voffice.ws_autosign.service.FileAttachTranfer;
import com.viettel.voffice.ws_autosign.service.FileAttachTranferList;
import com.viettel.voffice.ws_autosign.service.Vo2AutoSignSystemImpl;
import com.viettel.voffice.ws_autosign.service.Vo2AutoSignSystemImplService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileServiceImp extends BaseService implements FileService {
    @Value("${app.store.path-store-media}")
    String pathStore;
    @Value("${app.store.path-store-diagram}")
    String pathStoreDiagram;
    @Value("${app.store.extension-diagram}")
    String extensionDiagram;
    @Value("${app.store.path-store-export}")
    String pathExportStore;
    @Autowired
    HistoryUploadFileRepository historyUploadFileRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    Message message;

    @Autowired
    MapUtils mapUtils;

    @Autowired
    private VOConfig voConfig;

    public String getUploadFolder() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        return pathStore + "/" + dtf.format(localDate) + "/";

    }

    public String getFilePrefix() {
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmmss");

        return dtf.format(localTime);
    }

    @Override
    public String createFilePathReal(String fileName) {
        return getUploadFolder() + getFilePrefix() + fileName;
    }

    @Override
    public UploadDTO.UploadFileResponse storeFile(MultipartFile file) {
        if (file.getSize() == 0)
            throw new BadRequestException(message.getMessage("file.invalid"));
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = "";
        String extension = "";
        try {
            if (originalFileName.contains("..")) {
                throw new BadRequestException(message.getMessage("file.path_invalid"));
            }
            String dateString = DateUtils.getNowTime("yyyy-MM-dd");
            String[] dateParts = dateString.split("-");
            String year = dateParts[0];
            String month = dateParts[1];
            String day = dateParts[2];
            String UPLOADED_FOLDER = pathStore + "/" + year + "/" + month + "/" + day + "/";

            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");

            final Path fileStorageLocation = Paths.get(UPLOADED_FOLDER).toAbsolutePath().normalize();
            try {
                if (!Files.exists(fileStorageLocation))
                    Files.createDirectories(fileStorageLocation);
            } catch (Exception ex) {
                throw new RuntimeException(message.getMessage("file.directory_create"));
            }
            extension = FileUtils.getFileExtension(originalFileName);
            fileName = originalFileName.replace(extension, "");
            fileName = formatter.format(localDateTime) + FileUtils.getNameFileMD5(fileName, extension);
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            UploadDTO.UploadFileResponse response = new UploadDTO.UploadFileResponse();
            response.url = year + "-" + month + "-" + day + "-" + fileName;
            response.title = file.getOriginalFilename();
            response.size = targetLocation.toFile().length();
            response.absoluteUrl = targetLocation.toAbsolutePath().toString();
            response.encodeUrl = year + "-" + month + "-" + day + "-" + fileName;
            response.type = extension;
            // HistoryUploadEntity uploadFile = historyUploadFileRepository
            // .findByUrl(response.url)
            // .orElse(new HistoryUploadEntity());
            // Long oldId = uploadFile.getId();
            // mapUtils.customMap(objectMapper.convertValue(response,
            // HistoryUploadEntity.class), uploadFile);
            // if (oldId != null) uploadFile.setId(oldId);
            // historyUploadFileRepository.save(uploadFile);
            return response;
        } catch (IOException exception) {
            String stackTrace = ExceptionUtils.getStackTrace(exception);
            log.error(stackTrace);
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!");
        }
    }

    // @Override
    // public UploadDTO.UploadFileResponse storeFile(String originalFileName, byte[]
    // fileDate) {
    //
    // return multipartFile;
    // }

    @Override
    public List<UploadDTO.UploadFileResponse> storeFiles(MultipartFile[] files) {
        return Arrays.stream(files).map(this::storeFile).collect(Collectors.toList());
    }

    @Override
    public Resource downloadFileWithEncodePath(String encodePath) {
        return downloadFile(pathStore, encodePath, true);
    }

    @Override
    public Resource downloadFileWithRealPath(String encodePath) {
        return downloadFile(pathStore, encodePath, false);
    }

    @Override
    public List<Resource> downloadMultiFile(List<String> listFiles) {
        List<Resource> resourceList = new ArrayList<>();
        listFiles.forEach(fileName -> {
            resourceList.add(downloadFileWithEncodePath(fileName));
        });

        return resourceList;
    }

    private Resource downloadFile(String root, String fileSpecialPath, boolean isPathEncode) {
        try {
            String realPath = isPathEncode ? fileSpecialPath.replace("-", "/") : fileSpecialPath;
            Path fileStorageLocation = Paths.get(root, fileSpecialPath.replace("-", "/")).toAbsolutePath().normalize();
            log.info("Download from URI: {}", fileStorageLocation.toUri());
            Resource resource = new UrlResource(fileStorageLocation.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File không tồn tại");
            }
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
            throw new BadRequestException("File không tồn tại");
        }
    }

    @Override
    public String decodePath(String encodePath) {
        return pathStore + "/" + encodePath.replace("-", "/");
    }

    @Override
    public String encodePath(String realPath) {
        String localDateString = realPath.replace(pathStore + "/", "").substring(0, 11);
        String localDateStringEncode = localDateString.replace("/", "-");

        return realPath.replace(pathStore + "/", "").replace(localDateString, localDateStringEncode);
    }

    @Override
    public String getFileNameFromEncodePath(String encodePath) {
        return encodePath.substring(17);
    }

    @Override
    public String getFileNameFromEncodePathWithPrefix(String encodePath) {
        return encodePath.substring(11);
    }

    @Override
    public String getFileNameFromDecodePath(String decodePath) {
        return encodePath(decodePath).substring(17);
    }

    @Override
    public String getFileNameFromDecodePathWithPrefix(String decodePath) {
        return encodePath(decodePath).substring(11);
    }

    @Override
    public boolean createFolder(String encodePath) {
        File outputFile = new File(decodePath(encodePath));
        return outputFile.mkdirs();
    }

    @Override
    public Boolean removeFile(String fileName) {
        return removeFile(pathStore, fileName);
    }

    @Override
    public Boolean removeMultiFile(List<String> fileName) {
        for (int i = 0; i < fileName.size(); i++) {
            removeFile(pathStore, fileName.get(i));

            if (i == fileName.size() - 1)
                return true;
        }

        return false;
    }

    private Boolean removeFile(String root, String fileName) {
        try {
            Path fileStorageLocation = Paths.get(root, fileName.replace("-", "/")).toAbsolutePath().normalize();
            Resource resource = new UrlResource(fileStorageLocation.toUri());

            if (resource.exists()) {
                return org.apache.commons.io.FileUtils.deleteQuietly(resource.getFile());
            } else {
                throw new NotFoundException(message.getMessage("file.not_found"));
            }
        } catch (Exception ex) {
            throw new BadRequestException("File không tồn tại");
        }
    }

    @Override
    public String createFileText(String data) {
        String dateString = DateUtils.getNowTime("yyyy-MM-dd");
        String[] dateParts = dateString.split("-");
        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];
        String UPLOADED_FOLDER = pathStoreDiagram + "/" + year + "/" + month + "/" + day + "/";
        String fileName = DateUtils.getNow().getTime() + "";
        fileName = FileUtils.getNameFileMD5(fileName, extensionDiagram);
        final Path fileStorageLocation = Paths.get(UPLOADED_FOLDER, fileName).toAbsolutePath().normalize();
        try {
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation.getParent());
                Files.createFile(fileStorageLocation);
            }
        } catch (Exception ex) {
            throw new RuntimeException(message.getMessage("file.directory_create"));
        }
        boolean result = printTextToFile(fileStorageLocation.toAbsolutePath().toString(), data);
        if (result) {
            String url = dateString + "-" + fileName;
            UploadDTO.UploadFileResponse response = new UploadDTO.UploadFileResponse();
            response.url = url;
            response.title = fileName;
            response.size = fileStorageLocation.toFile().length();
            response.type = extensionDiagram;
            HistoryUploadEntity uploadFile = historyUploadFileRepository.findByUrl(url)
                    .orElse(new HistoryUploadEntity());
            HistoryUploadEntity convertedResponse = objectMapper.convertValue(response, HistoryUploadEntity.class);
            modelMapper.map(convertedResponse, uploadFile);
            historyUploadFileRepository.save(uploadFile);
            return url;
        } else
            return null;
    }

    @Override
    public String updateFileText(String oldPath, String data) {
        Path fileStorageLocation = Paths.get(pathStoreDiagram, oldPath.replace("-", "/")).toAbsolutePath().normalize();
        if (!(Files.exists(fileStorageLocation)))
            throw new NotFoundException(message.getMessage("file_not_found"));
        boolean result = printTextToFile(fileStorageLocation.toAbsolutePath().toString(), data);
        if (result)
            return oldPath;
        else
            return null;
    }

    @Override
    public String readFileText(String path) {
        return readerFileToText(path);
    }

    @Override
    public FileDTO.FileResponse downloadExportFile(String fileName) {
        try {
            File file = new File(pathExportStore + File.separator + fileName);
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            String contentType = Files.probeContentType(file.toPath());
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = fileName;
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (Exception ex) {
        }
        return null;

    }

    private boolean printTextToFile(String file, String data) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
            writer.write(data);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null)
                writer.close();
        }

        return true;
    }

    private String readerFileToText(String file) {
        Path fileStorageLocation = Paths.get(pathStoreDiagram, file.replace("-", "/")).toAbsolutePath().normalize();
        if (!(Files.exists(fileStorageLocation)))
            throw new NotFoundException(message.getMessage("file_not_found"));

        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(fileStorageLocation, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return contentBuilder.toString();
    }

    @Override
    public FileDTO.FileResponse downloadFileFromVO(HttpServletRequest request, String filePath, String transCode) {
        FileAttachTranferList fileAttachTranferList = viewSignedDoc(transCode);
        Resource resource = downloadFileWithEncodePath(filePath);
        String fileNameRaw = getFileNameFromEncodePath(filePath);
        String filePathReal = decodePath(filePath);

        FileAttachTranfer fileAttachTranfer = fileAttachTranferList.getLstFileAttachTranfer().stream()
                .filter(item -> item.getFileName().contains(fileNameRaw)).findFirst().orElse(null);
        if (fileAttachTranfer == null) {
            throw new NotFoundException("Không đọc được file trên vOffice");
        }
        String contentType = null;

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Không thể xác định được mine-file: " + filePath);
            contentType = "application/octet-stream";
        }

        byte[] data = Base64.getEncoder().encode(fileAttachTranfer.getAttachBytes());

        FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
        fileDTO.data = new String(data, StandardCharsets.US_ASCII);
        fileDTO.encodePath = filePath;
        fileDTO.filePath = filePath;
        fileDTO.fileName = fileNameRaw;
        fileDTO.size = (long) (data.length);
        fileDTO.type = contentType;
        return fileDTO;
    }

    private FileAttachTranferList viewSignedDoc(String transCode) {
        Vo2AutoSignSystemImplService sv = null;
        try {
            sv = new Vo2AutoSignSystemImplService(new URL(voConfig.ca_wsUrl));
            Vo2AutoSignSystemImpl service = sv.getVo2AutoSignSystemImplPort();
            FileAttachTranferList file = service.getFile(voConfig.ca_appCode, transCode, true);
            if (file == null) {
                return service.getFile("CTCT_WMS", transCode, true);
            }
            // File.separator
            return file;
        } catch (MalformedURLException e) {
            throw new NotFoundException("Không tồn tại đường dẫn file");
        }
    }

}
