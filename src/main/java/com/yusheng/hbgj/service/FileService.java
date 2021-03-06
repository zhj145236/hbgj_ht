package com.yusheng.hbgj.service;

import java.io.IOException;

import com.yusheng.hbgj.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

public interface FileService {

	FileInfo save(MultipartFile file,FileInfo uploadFileInfo, HttpSession session) throws IOException;

	void delete(String id);

    void saveRemark(FileInfo fileInfo);

     String  getByUrl(String url);



     String getFileType(String fileName);

     int fileRefCount(String resourceId);


}
