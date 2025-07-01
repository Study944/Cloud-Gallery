package com.cloudgallery.manager.cos;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
@Component
public class UrlUploadManager extends ImageUploadTemplate{

    String imageSuffix;

    @Override
    void validator(Object dataSource) {
        // 1.校验请求协议
        String url = (String) dataSource;
        ThrowUtil.throwIf(
                !url.startsWith("http://") && !url.startsWith("https://"),
                ErrorCode.PARAMS_ERROR, "请求协议错误");
        // 2.校验文件后缀
        // 修改为 GET 请求以获取完整响应头
        HttpResponse httpResponse = HttpUtil.createRequest(Method.GET, url).execute();
        ThrowUtil.throwIf(!httpResponse.isOk(), ErrorCode.PARAMS_ERROR, "请求url错误");
        String contentType = httpResponse.header("Content-Type");
        List<String> ALLOW_CONTENT_TYPE = List.of("image/jpg","image/jpeg", "image/png", "image/gif");
        
        // 保留原有扩展名校验逻辑作为兜底
        if (!ALLOW_CONTENT_TYPE.contains(contentType)) {
            String path = url.split("\\?")[0]; // 去除查询参数
            String ext = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
            ThrowUtil.throwIf(!List.of("jpg","jpeg","png","gif").contains(ext), 
                ErrorCode.PARAMS_ERROR, "url图片格式错误");
            imageSuffix = ext;
        } else {
            imageSuffix = contentType.substring(contentType.lastIndexOf("/") + 1);
        }
        // 3.校验文件大小
        final long maxSize = 1024 * 1024 * 10;
        ThrowUtil.throwIf(httpResponse.contentLength() > maxSize, ErrorCode.PARAMS_ERROR, "文件大小不能超过10M");
        // 4.关闭流（GET请求需要显式关闭）
        httpResponse.close();
    }

    @Override
    String getFilename(Object dataSource){
        String url = (String) dataSource;
        String filename = url.substring(url.lastIndexOf('/') + 1);
        // 清理非法字符
        return sanitizeFilename(filename)+"."+imageSuffix;
    }

    private String sanitizeFilename(String filename) {
        String baseName = filename.split("\\?")[0];
        return baseName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    @Override
    File getFileBySource(Object dataSource, File file) {
        String url = (String) dataSource;
        HttpUtil.downloadFile(url, file);
        return file;
    }
}
