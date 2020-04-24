package com.yusheng.hbgj.controller;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.annotation.PermissionTag;
import com.yusheng.hbgj.constants.BusinessException;
import com.yusheng.hbgj.dao.FileInfoDao;
import com.yusheng.hbgj.dao.NoticeDao;
import com.yusheng.hbgj.dto.FileDto;
import com.yusheng.hbgj.dto.LayuiFile;
import com.yusheng.hbgj.entity.FileInfo;
import com.yusheng.hbgj.entity.Notice;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.service.FileService;
import com.yusheng.hbgj.utils.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jinwei
 * @date 2020-04-16
 * @desc
 */


@Api(tags = "文件")
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private FileInfoDao fileInfoDao;

    @Autowired
    private NoticeDao noticeDao;


    @Value("${constants.domain}")
    private String domain;


    @Value("${constants.hlgjId}")
    private String hlgjId;

    @Value("${files.path}")
    private String filesPath;


    @GetMapping("/{year}/{month}/{day}/{filename}")
    @ApiOperation(value = "原始文件下载")
    public void down(@PathVariable() String year, @PathVariable() String month, @PathVariable() String day, @PathVariable() String filename, HttpServletResponse response) throws IOException {


        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(File.separator).append(year).append(File.separator).append(month).append(File.separator).append(day).append(File.separator).append(filename);
        String fileOriginName = fileService.getByUrl(baseUrl.toString().replaceAll("\\\\", "/"));


        StringBuilder fullPath = new StringBuilder();
        fullPath.append(filesPath).append(File.separator).append(baseUrl);

        response.setContentType("application/octet-stream");
        if (StringUtils.isEmpty(fileOriginName)) {
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(), "iso-8859-1"));
        } else {
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileOriginName.getBytes(), "iso-8859-1"));
        }

        rejectStream(fullPath.toString(), response);


    }


    private void rejectStream(String fullPath, HttpServletResponse response) throws IOException {

        File file = new File(fullPath.toString());
        InputStream fis = new BufferedInputStream(new FileInputStream(fullPath.toString()));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();

        response.addHeader("Content-Length", "" + file.length());
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());


        toClient.write(buffer);
        toClient.flush();
        toClient.close();


    }


    @GetMapping("/prev/{year}/{month}/{day}/{filename}")
    @ApiOperation(value = "文件预览", tags = "适用于图片和PDF文档")
    public void prev(@PathVariable() String year, @PathVariable() String month, @PathVariable() String day, @PathVariable() String filename, @RequestParam(required = false) Integer isBigPic, HttpServletResponse response) throws IOException {


        StringBuilder fullPath = new StringBuilder();
        fullPath.append(filesPath).append(File.separator).append(File.separator).append(year).append(File.separator).append(month).append(File.separator).append(day).append(File.separator).append(filename);

        if (fullPath.toString().length() < 10) {
            return;
        }

        File file = new File(fullPath.toString());

        if (file.exists()) {

            String type = fileService.getFileType(file.getName());

            if ("图片".equals(type)) {

                //缓存7天
                response.setDateHeader("Expires", +System.currentTimeMillis() + 604800000);
                response.setContentType(this.trans(file.getName()));

                if (isBigPic != null && isBigPic == 1) {

                    rejectStream(fullPath.toString(), response);

                } else {

                    // 缩略图 缩小到0.3
                    Thumbnails.of(fullPath.toString()).scale(0.3f).toOutputStream(response.getOutputStream());
                }

            } else if ("文档".equals(type)) {

                //文件缓存7天
                response.setDateHeader("Expires", +System.currentTimeMillis() + 604800000);

                response.setContentType(this.trans(file.getName()));

                rejectStream(fullPath.toString(), response);

                response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(), StandardCharsets.ISO_8859_1));


            }
        }

    }

    private String trans(String fileName) {

        String tty = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        Map<String, String> map = new HashMap<String, String>(10);
        map.put("jpg", "image/jpeg");
        map.put("png", "image/png");
        map.put("gif", "image/gif");
        map.put("bmp", "application/x-bmp");
        map.put("tif", "image/tiff");
        map.put("jpeg", "image/jpeg");
        map.put("ico", "image/x-icon");
        map.put("pdf", "application/pdf");

        return map.get(tty) == null ? "image/jpeg" : map.get(tty);


    }


    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "文件上传")
    public FileInfo uploadFile(MultipartFile file, FileInfo fileInfo, HttpSession session) throws IOException {
        if (fileInfo == null) {
            fileInfo = new FileInfo();
        }
        FileInfo newFile = fileService.save(file, fileInfo, session);

        newFile.setRemark(domain + "/files" + newFile.getUrl());

        return newFile;

    }


    /**
     * layui富文本文件自定义上传
     *
     * @param file
     * @return
     * @throws IOException
     */
    @LogAnnotation
    @PostMapping("/layui")
    @ApiOperation(value = "layui富文本文件自定义上传")
    public LayuiFile uploadLayuiFile(MultipartFile file, HttpSession session, @RequestParam(required = false) String tag) throws IOException {

        FileInfo ff = new FileInfo();

        ff.setTag(StringUtils.isEmpty(tag) ? "富文本内文件" : tag);

        FileInfo fileInfo = fileService.save(file, ff, session);

        LayuiFile layuiFile = new LayuiFile();
        layuiFile.setCode(0);
        LayuiFile.LayuiFileData data = new LayuiFile.LayuiFileData();
        layuiFile.setData(data);


        //域名问题
        data.setSrc(domain + "/files" + fileInfo.getUrl());

        data.setTitle(file.getOriginalFilename());

        return layuiFile;
    }

    @GetMapping
    @ApiOperation(value = "文件查询")
    @PermissionTag("sys:file:query")
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


                return fileInfoDao.list(request.getParams(), request.getOffset(), request.getLimit());

            }
        }).handle(request);
    }

    @GetMapping("/wxlistFiles")
    @ApiOperation(value = "小程序端数据中心", tags = "企业检修资料；企业台账；合同；环保文件, 需要开放查询的权限才能调用")
    @PermissionTag("sys:file:query")
    public PageTableResponse wxlistFiles(PageTableRequest request) {


        if (StringUtils.isEmpty(request.getParams().get("resourceId"))) {

            throw new IllegalArgumentException("resourceId不能为空");

        }


        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return fileInfoDao.wxCount(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<FileDto> list(PageTableRequest request) {


                request.getParams().putIfAbsent("orderBy", "  ORDER BY uploadTime DESC  ");

                return fileInfoDao.wxlistFiles(request.getParams(), request.getOffset(), request.getLimit());


            }
        }).handle(request);
    }


    @LogAnnotation
    @DeleteMapping("/{id}")
    @ApiOperation(value = "文件删除")
    @PermissionTag("sys:file:del")
    public void delete(@PathVariable String id) {

        fileService.delete(id);

        //删除之前可能预设的系统通知
        noticeDao.flushNotice(id);


    }


    @LogAnnotation
    @PutMapping("/saveRemark")
    @ApiOperation(value = "更新合同起止时间")
    @PermissionTag("sys:file:update")
    public void saveRemark(@RequestBody FileInfo fileInfo) {


        validDate(fileInfo.getRemark());

        fileService.saveRemark(fileInfo);

        senNotice(fileInfo.getId(), fileInfo.getRemark().trim().split("@")[1].trim());

        // 立刻发布通知
        noticeDao.autoPublish();


    }


    private void validDate(String dateStr) {

        if (StringUtils.isEmpty(dateStr)) {

            throw new IllegalArgumentException("合同起止时间都不能为空");

        }

        String[] date = dateStr.trim().split("@");

        Date dateStart = DateUtil.parseDate(date[0].trim());
        Date dateEnd = DateUtil.parseDate(date[1].trim());

        if (DateUtil.daysBetween(dateStart, dateEnd) < 1) {
            throw new BusinessException("合同结束时间应该大于开始时间");
        }


    }


    /**
     * 给环联管家平台发通知
     */
    private void senNotice(String fileId, String endDate) {


        FileInfo file = fileInfoDao.getById(fileId);


        // 30天前；7天前；1天前通知
        Date[] dates = new Date[]{DateUtil.addDay(DateUtil.parseDate(endDate), -30), DateUtil.addDay(DateUtil.parseDate(endDate), -7), DateUtil.addDay(DateUtil.parseDate(endDate), -1)};


        noticeDao.flushNotice(fileId);

        for (int i = 0; i < dates.length; i++) {


            if (DateUtil.daysBetween(new Date(), dates[i]) >= 0) {


                Notice notice = new Notice();
                notice.setIsPersonal(Notice.Personal.YES);
                notice.setTitle("合同快到期提醒");
                notice.setReceiveId(hlgjId);

                // 给东莞市环联管家生态环境科技有限公司发通知
                String sb = "您有一份与" + file.getOrgId() + "签订的合同【" + file.getFileOriginName() + "】" +
                        "将于" + endDate +
                        "到期,请及时处理。合同附件链接 【<a target='_blank'  style='color:blue;font-size:23px' href='" + domain + "/files" + file.getUrl() + "'>打开</a>" + "】。如已经处理请忽略此条通知";
                notice.setContent(sb);
                notice.setCreateName("系统程序");
                //设置合同到期前X天创建
                notice.setCreateTime(dates[i]);
                notice.setRefId(fileId);
                //草稿
                notice.setStatus(Notice.Status.DRAFT);
                notice.setUpdateTime(new Date());
                noticeDao.save(notice);


                // 给厂商也要发通知
                Notice notice2 = new Notice();
                notice2.setIsPersonal(Notice.Personal.YES);
                notice2.setTitle("合同快到期提醒");
                //厂商ID
                notice2.setReceiveId(file.getResourceId());

                String sb2 = "您有一份与" + "东莞市环联管家生态环境科技有限公司" + "签订的合同【" + file.getFileOriginName() + "】" +
                        "将于" + endDate +
                        "到期,请及时处理。如已经处理请忽略此条通知";
                notice2.setContent(sb2);
                //草稿
                notice2.setStatus(Notice.Status.DRAFT);
                notice2.setCreateName("系统程序");
                notice2.setUpdateTime(new Date());
                //设置合同到期前X天创建
                notice2.setCreateTime(dates[i]);
                notice2.setRefId(fileId);

                noticeDao.save(notice2);


            }
        }


    }

}