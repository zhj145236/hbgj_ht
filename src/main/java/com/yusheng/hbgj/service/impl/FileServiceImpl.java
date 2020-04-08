package com.yusheng.hbgj.service.impl;

import com.yusheng.hbgj.dao.FileInfoDao;
import com.yusheng.hbgj.entity.FileInfo;
import com.yusheng.hbgj.service.FileService;
import com.yusheng.hbgj.utils.FileUtil;
import com.yusheng.hbgj.utils.StrUtil;
import com.yusheng.hbgj.utils.UserUtil2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Value("${files.path}")
    private String filesPath;

    @Autowired
    private FileInfoDao fileInfoDao;

    @Override
    public FileInfo save(MultipartFile file, FileInfo uploadFileInfo, HttpSession session) throws IOException {
        String fileOrigName = file.getOriginalFilename();
        if (!fileOrigName.contains(".")) {
            throw new IllegalArgumentException("缺少后缀名");
        }

        String md5 = FileUtil.fileMd5(file.getInputStream());

        /*FileInfo fileInfo = fileInfoDao.getById(md5);

        if (fileInfo != null) {
            fileInfoDao.update(fileInfo);
            return fileInfo;
        }*/

        FileInfo fileInfo = new FileInfo();

        fileOrigName = fileOrigName.substring(fileOrigName.lastIndexOf("."));
        String pathname = FileUtil.getPath() + md5 + fileOrigName;
        String fullPath = filesPath + pathname;
        FileUtil.saveFile(file, fullPath);

        long size = file.getSize();
        String contentType = file.getContentType();


        fileInfo.setId(StrUtil.uuid());
        fileInfo.setContentType(contentType);
        fileInfo.setSize(size);
        fileInfo.setPath(fullPath);
        fileInfo.setUrl(pathname);
        fileInfo.setType(getFileType(file.getOriginalFilename()));

        fileInfo.setUploadTime(new Date());
        fileInfo.setFileOriginName(file.getOriginalFilename());

        String createName = UserUtil2.getCurrentUser().getUsername();

        fileInfo.setCreateName(createName);

        fileInfo.setCreateTime(new Date());
        fileInfo.setMd5(md5);

        // 富文本编辑器上传附件可能使得uploadFileInfo为空
        if (uploadFileInfo != null) {
            fileInfo.setTag(uploadFileInfo.getTag());
            fileInfo.setResourceId(uploadFileInfo.getResourceId());
            fileInfo.setRemark(uploadFileInfo.getRemark());
            fileInfo.setOrgId(uploadFileInfo.getOrgId());
        }
        fileInfoDao.save(fileInfo);


        log.debug("上传文件{}成功", fullPath);

        return fileInfo;

    }


    @Override
    public String getFileType(String fileName) {

        //VID_20190617_185050.mp4.zIP

        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        String tty = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();

        String[] images = new String[]{"jpg", "png", "gif", "bmp", "tif", "jpeg", "gif", "ico"};
        String[] doc = new String[]{"pdf", "xls", "xlsx", "doc", "docx", "ppt", "pptx", "txt"};
        String[] exe = new String[]{"exe", "bat", "cmd", "vbs", "js", "dll"};
        String[] zip = new String[]{"zip", "rar", "tar", "jar", "7z"};
        String[] video = new String[]{"mp4", "avi", "rmvb", "mov", "wvm"};
        String[] audio = new String[]{"mp3", "wav", "mid", "aac", "ogg"};


        Map<String, String[]> types = new HashMap<>();
        types.put("图片", images);
        types.put("文档", doc);
        types.put("可执行文件", exe);
        types.put("压缩文件", zip);
        types.put("视频", video);
        types.put("音频", audio);


        Iterator map1it = types.entrySet().iterator();
        while (map1it.hasNext()) {
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) map1it.next();


            String[] value = entry.getValue();
            for (int i = 0; i < value.length; i++) {

                if (value[i].equals(tty)) {

                    return entry.getKey();
                }
            }

        }


        return "未知类型";


    }


    @Override
    public void delete(String id) {
        FileInfo fileInfo = fileInfoDao.getById(id);
        if (fileInfo != null) {
            String fullPath = fileInfo.getPath();
            FileUtil.deleteFile(fullPath);

            fileInfoDao.delete(id);
            log.debug("删除文件：{}", fileInfo.getPath());
        }
    }

    @Override
    public void saveRemark(FileInfo fileInfo) {

        FileInfo ff = new FileInfo();
        ff.setId(fileInfo.getId());
        ff.setRemark(fileInfo.getRemark());

        fileInfoDao.saveRemark(ff);

    }

    @Override
    public String getByUrl(String url) {

        return fileInfoDao.getByUrl(url);

    }



}
