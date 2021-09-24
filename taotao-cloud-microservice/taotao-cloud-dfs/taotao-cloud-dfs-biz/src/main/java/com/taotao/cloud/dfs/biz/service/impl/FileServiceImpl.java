package com.taotao.cloud.dfs.biz.service.impl;

import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.dfs.biz.entity.File;
import com.taotao.cloud.dfs.biz.repository.FileRepository;
import com.taotao.cloud.dfs.biz.service.IFileService;
import com.taotao.cloud.file.service.UploadFileService;
import com.taotao.cloud.file.exception.UploadFileException;
import com.taotao.cloud.file.model.UploadFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * 文件上传服务
 *
 * @author shuigedeng
 * @since 2020/11/12 17:43
 * @version 1.0.0
 */
@Service
public class FileServiceImpl implements IFileService {
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private UploadFileService uploadFileService;

	@Override
	public File upload(MultipartFile file) {
		try {
			UploadFileInfo upload = uploadFileService.upload(file);

			// 添加文件数据
			return new File();
		} catch (UploadFileException e) {
			throw new BusinessException(e.getMessage());
		}
	}

	@Override
	public File findFileById(Long id) {
		Optional<File> optionalFile = fileRepository.findById(id);
		return optionalFile.orElseThrow(() -> new BusinessException(ResultEnum.FILE_NOT_EXIST));
	}
	//
	// @Override
	// public Result<Object> delete(String objectName) {
	// 	try {
	// 		ossClient.deleteObject(ossConfig.getBucketName(), objectName);
	// 	} catch (OSSException | ClientException e) {
	// 		e.printStackTrace();
	// 		return new Result<>(ResultCode.ERROR.getCode(), "删除文件失败");
	// 	}
	// 	return new Result<>();
	// }
	//
	// @Override
	// public Result<List<OSSObjectSummary>> list() {
	// 	// 设置最大个数。
	// 	final int maxKeys = 100;
	//
	// 	ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(ossConfig.getBucketName()).withMaxKeys(maxKeys));
	// 	List<OSSObjectSummary> result = objectListing.getObjectSummaries();
	// 	return new Result<List<OSSObjectSummary>>(result);
	// }
	//
	// @Override
	// public void exportOssFile(ServletOutputStream outputStream, String objectName) {
	// 	OSSObject ossObject = ossClient.getObject(ossConfig.getBucketName(), objectName);
	//
	// 	BufferedInputStream in = new BufferedInputStream(ossObject.getObjectContent());
	// 	BufferedOutputStream out = new BufferedOutputStream(outputStream);
	// 	try {
	// 		byte[] buffer = new byte[1024];
	// 		int lenght;
	// 		while ((lenght = in.read(buffer)) != -1) {
	// 			out.write(buffer, 0, lenght);
	// 		}
	//
	// 		out.flush();
	// 		out.close();
	// 		in.close();
	// 	} catch (IOException e) {
	// 		e.printStackTrace();
	// 	}
	// }
}
