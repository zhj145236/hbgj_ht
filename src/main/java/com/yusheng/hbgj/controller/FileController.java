package com.yusheng.hbgj.controller;

import java.io.IOException;
import java.util.List;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.dao.FileInfoDao;
import com.yusheng.hbgj.dto.LayuiFile;
import com.yusheng.hbgj.entity.FileInfo;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.service.FileService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "文件")
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private FileInfoDao fileInfoDao;

    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "文件上传")
    public FileInfo uploadFile(MultipartFile file, FileInfo fileInfo) throws IOException {
        if (fileInfo == null) {
            fileInfo = new FileInfo();
        }
        return fileService.save(file, fileInfo);
    }


    /**
     * layui富文本文件自定义上传
     *
     * @param file
     * @param domain
     * @return
     * @throws IOException
     */
    @LogAnnotation
    @PostMapping("/layui")
    @ApiOperation(value = "layui富文本文件自定义上传")
    public LayuiFile uploadLayuiFile(MultipartFile file, String domain) throws IOException {
        FileInfo fileInfo = fileService.save(file,null);

        LayuiFile layuiFile = new LayuiFile();
        layuiFile.setCode(0);
        LayuiFile.LayuiFileData data = new LayuiFile.LayuiFileData();
        layuiFile.setData(data);
        data.setSrc(domain + "/files" + fileInfo.getUrl());
        data.setTitle(file.getOriginalFilename());

        return layuiFile;
    }

    @GetMapping
    @ApiOperation(value = "文件查询")
    @RequiresPermissions("sys:file:query")
    public PageTableResponse listFiles(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return fileInfoDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<FileInfo> list(PageTableRequest request) {


                request.getParams().putIfAbsent("orderBy", "  ORDER BY uploadTime DESC  ");


                List<FileInfo> list = fileInfoDao.list(request.getParams(), request.getOffset(), request.getLimit());
                return list;
            }
        }).handle(request);
    }

    @LogAnnotation
    @DeleteMapping("/{id}")
    @ApiOperation(value = "文件删除")
    @RequiresPermissions("sys:file:del")
    public void delete(@PathVariable String id) {
        fileService.delete(id);
    }


    @LogAnnotation
    @PutMapping("/saveRemark")
    @ApiOperation(value = "更新文件信息")
    @RequiresPermissions("sys:file:del")
    public void saveRemark(@RequestBody FileInfo fileInfo) {


        fileService.saveRemark(fileInfo);
    }

}
