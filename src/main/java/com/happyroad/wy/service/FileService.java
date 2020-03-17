package com.happyroad.wy.service;

import java.io.IOException;

import com.happyroad.wy.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	FileInfo save(MultipartFile file) throws IOException;

	void delete(String id);

}
