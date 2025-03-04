package com.taotao.cloud.workflow.biz.engine.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowEngineEntity;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowTaskStatusEnum;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.FlowEngineCrForm;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.FlowEngineInfoVO;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.FlowEngineListVO;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.FlowEngineModel;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.FlowEngineSelectVO;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.FlowEngineUpForm;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.FlowExportModel;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.FlowPageListVO;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.FlowPagination;
import com.taotao.cloud.workflow.biz.engine.model.flowengine.PaginationFlowEngine;
import com.taotao.cloud.workflow.biz.engine.service.FlowEngineService;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskService;
import com.taotao.cloud.workflow.biz.engine.util.FormCloumnUtil;
import com.taotao.cloud.workflow.biz.engine.util.ServiceAllUtil;
import com.taotao.cloud.workflow.biz.engine.util.VisualDevTableCre;
import com.taotao.cloud.workflow.biz.model.FormAllModel;
import com.taotao.cloud.workflow.biz.model.FormEnum;
import com.taotao.cloud.workflow.biz.model.RecursionForm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.swing.table.TableModel;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 流程设计
 */
@Tag(tags = "流程引擎", value = "FlowEngine")
@RestController
@RequestMapping("/api/workflow/Engine/FlowEngine")
public class FlowEngineController {

    @Autowired
    private FlowEngineService flowEngineService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private DataFileExport fileExport;
    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private VisualDevTableCre visualDevTableCre;

    /**
     * 获取流程设计列表
     *
     * @return
     */
    @Operation("获取流程引擎列表")
    @GetMapping
    public Result list(FlowPagination pagination) {
        List<FlowEngineEntity> list = flowEngineService.getPageList(pagination);
        List<DictionaryDataEntity> dictionList = serviceUtil.getDictionName(list.stream().map(t -> t.getCategory()).collect(Collectors.toList()));
        for (FlowEngineEntity entity : list) {
            DictionaryDataEntity dataEntity = dictionList.stream().filter(t -> t.getEnCode().equals(entity.getCategory())).findFirst().orElse(null);
            entity.setCategory(dataEntity != null ? dataEntity.getFullName() : "");
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        List<FlowPageListVO> listVO = JsonUtil.getJsonToList(list, FlowPageListVO.class);
        return Result.page(listVO, paginationVO);
    }

    /**
     * 获取流程设计列表
     *
     * @return
     */
    @Operation("流程引擎下拉框")
    @GetMapping("/Selector")
    public Result<ListVO<FlowEngineListVO>> listSelect(Integer type) {
        PaginationFlowEngine pagination = new PaginationFlowEngine();
        pagination.setFormType(type);
        pagination.setEnabledMark(1);
        pagination.setType(0);
        List<FlowEngineListVO> treeList = flowEngineService.getTreeList(pagination, true);
        ListVO vo = new ListVO();
        vo.setList(treeList);
        return Result.success(vo);
    }

    /**
     * 主表属性
     *
     * @return
     */
    @Operation("表单主表属性")
    @GetMapping("/{id}/FormDataFields")
    public Result<ListVO<FormDataField>> getFormDataField(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        List<FormDataField> formDataFieldList = new ArrayList<>();
        if (entity.getFormType() == 1) {
            List<FlowEngineModel> list = JsonUtil.getJsonToList(entity.getFormData(), FlowEngineModel.class);
            for (FlowEngineModel model : list) {
                FormDataField formDataField = new FormDataField();
                formDataField.setLabel(model.getFiledName());
                formDataField.setVModel(model.getFiledId());
                formDataFieldList.add(formDataField);
            }
        } else {
            //formTempJson
            FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
            List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
            List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getFlowTables(), TableModel.class);
            List<FormAllModel> formAllModel = new ArrayList<>();
            RecursionForm recursionForm = new RecursionForm(list, tableModelList);
            FormCloumnUtil.recursionForm(recursionForm, formAllModel);
            //主表数据
            List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
            for (FormAllModel model : mast) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                String vmodel = fieLdsModel.getVModel();
                String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
                if (StringUtil.isNotEmpty(vmodel) && !JnpfKeyConsts.RELATIONFORM.equals(jnpfKey) && !JnpfKeyConsts.RELATIONFLOW.equals(jnpfKey)) {
                    FormDataField formDataField = new FormDataField();
                    formDataField.setLabel(fieLdsModel.getConfig().getLabel());
                    formDataField.setVModel(fieLdsModel.getVModel());
                    formDataFieldList.add(formDataField);
                }
            }
        }
        ListVO<FormDataField> listVO = new ListVO();
        listVO.setList(formDataFieldList);
        return Result.success(listVO);
    }

    /**
     * 列表
     *
     * @return
     */
    @Operation("表单列表")
    @GetMapping("/{id}/FieldDataSelect")
    public Result<ListVO<FlowEngineSelectVO>> getFormData(@PathVariable("id") String id) {
        List<FlowTaskEntity> flowTaskList = flowTaskService.getTaskList(id).stream().filter(t -> FlowTaskStatusEnum.Adopt.getCode().equals(t.getStatus())).collect(Collectors.toList());
        List<FlowEngineSelectVO> vo = new ArrayList<>();
        for (FlowTaskEntity taskEntity : flowTaskList) {
            FlowEngineSelectVO selectVO = JsonUtil.getJsonToBean(taskEntity, FlowEngineSelectVO.class);
            selectVO.setFullName(taskEntity.getFullName() + "/" + taskEntity.getEnCode());
            vo.add(selectVO);
        }
        ListVO listVO = new ListVO();
        listVO.setList(vo);
        return Result.success(listVO);
    }

    /**
     * 可见引擎下拉框
     *
     * @return
     */
    @Operation("可见引擎下拉框")
    @GetMapping("/ListAll")
    public Result<ListVO<FlowEngineListVO>> listAll() {
        PaginationFlowEngine pagination = new PaginationFlowEngine();
        List<FlowEngineListVO> treeList = flowEngineService.getTreeList(pagination, false);
        ListVO vo = new ListVO();
        vo.setList(treeList);
        return Result.success(vo);
    }

    /**
     * 可见的流程引擎列表
     *
     * @return
     */
    @Operation("可见的流程引擎列表")
    @GetMapping("/PageListAll")
    public Result<PageListVO<FlowPageListVO>> listAll(FlowPagination pagination) {
        List<FlowEngineEntity> list = flowEngineService.getListAll(pagination, true);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        List<FlowPageListVO> listVO = JsonUtil.getJsonToList(list, FlowPageListVO.class);
        return Result.page(listVO, paginationVO);
    }

    /**
     * 获取流程引擎信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取流程引擎信息")
    @GetMapping("/{id}")
    public Result<FlowEngineInfoVO> info(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity flowEntity = flowEngineService.getInfo(id);
        FlowEngineInfoVO vo = JsonUtil.getJsonToBean(flowEntity, FlowEngineInfoVO.class);
        return Result.success(vo);
    }

    /**
     * 新建流程设计
     *
     * @return
     */
    @Operation("新建流程引擎")
    @PostMapping
    public Result create(@RequestBody @Valid FlowEngineCrForm flowEngineCrForm) throws WorkFlowException {
        FlowEngineEntity flowEngineEntity = JsonUtil.getJsonToBean(flowEngineCrForm, FlowEngineEntity.class);
        if (flowEngineService.isExistByFullName(flowEngineEntity.getFullName(), flowEngineEntity.getId())) {
            return Result.fail("流程名称不能重复");
        }
        if (flowEngineService.isExistByEnCode(flowEngineEntity.getEnCode(), flowEngineEntity.getId())) {
            return Result.fail("流程编码不能重复");
        }
        if (flowEngineEntity.getFormType() != 1) {
            FormDataModel formData = JsonUtil.getJsonToBean(flowEngineEntity.getFormData(), FormDataModel.class);
            List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
            List<TableModel> tableModelList = JsonUtil.getJsonToList(flowEngineEntity.getFlowTables(), TableModel.class);
            RecursionForm recursionForm = new RecursionForm(list, tableModelList);
            List<FormAllModel> formAllModel = new ArrayList<>();
            if (FormCloumnUtil.repetition(recursionForm, formAllModel)) {
                return Result.fail("子表重复");
            }
        }
        flowEngineService.create(flowEngineEntity);
        return Result.success(MsgCode.SU001.get());
    }

    /**
     * 更新流程设计
     *
     * @param id 主键值
     * @return
     */
    @Operation("更新流程引擎")
    @PutMapping("/{id}")
    public Result update(@PathVariable("id") String id, @RequestBody @Valid FlowEngineUpForm flowEngineUpForm) throws WorkFlowException {
        FlowEngineEntity flowEngineEntity = JsonUtil.getJsonToBean(flowEngineUpForm, FlowEngineEntity.class);
        if (flowEngineService.isExistByFullName(flowEngineUpForm.getFullName(), id)) {
            return Result.fail("流程名称不能重复");
        }
        if (flowEngineService.isExistByEnCode(flowEngineUpForm.getEnCode(), id)) {
            return Result.fail("流程编码不能重复");
        }
        if (flowEngineEntity.getFormType() != 1) {
            FormDataModel formData = JsonUtil.getJsonToBean(flowEngineEntity.getFormData(), FormDataModel.class);
            List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
            List<TableModel> tableModelList = JsonUtil.getJsonToList(flowEngineEntity.getFlowTables(), TableModel.class);
            RecursionForm recursionForm = new RecursionForm(list, tableModelList);
            List<FormAllModel> formAllModel = new ArrayList<>();
            if (FormCloumnUtil.repetition(recursionForm, formAllModel)) {
                return Result.fail("子表重复");
            }
        }
        boolean flag = flowEngineService.updateVisible(id, flowEngineEntity);
        if (flag == false) {
            return Result.success(MsgCode.FA002.get());
        }
        return Result.success(MsgCode.SU004.get());
    }

    /**
     * 删除流程设计
     *
     * @param id 主键值
     * @return
     */
    @Operation("删除流程引擎")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        List<FlowTaskEntity> taskNodeList = flowTaskService.getTaskList(entity.getId());
        if (taskNodeList.size() > 0) {
            return Result.fail("引擎在使用，不可删除");
        }
        flowEngineService.delete(entity);
        return Result.success(MsgCode.SU003.get());
    }

    /**
     * 复制流程表单
     *
     * @param id 主键值
     * @return
     */
    @Operation("复制流程表单")
    @PostMapping("/{id}/Actions/Copy")
    public Result copy(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity flowEngineEntity = flowEngineService.getInfo(id);
        if (flowEngineEntity != null) {
            String copyNum = UUID.randomUUID().toString().substring(0, 5);
            flowEngineEntity.setFullName(flowEngineEntity.getFullName() + ".副本" + copyNum);
            flowEngineEntity.setEnCode(flowEngineEntity.getEnCode() + copyNum);
            flowEngineEntity.setCreatorTime(new Date());
            flowEngineEntity.setId(null);
            flowEngineService.copy(flowEngineEntity);
            return Result.success(MsgCode.SU007.get());
        }
        return Result.fail(MsgCode.FA004.get());
    }

    /**
     * 流程表单状态
     *
     * @param id 主键值
     * @return
     */
    @Operation("更新流程表单状态")
    @PutMapping("/{id}/Actions/State")
    public Result state(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark("1".equals(String.valueOf(entity.getEnabledMark())) ? 0 : 1);
            flowEngineService.update(id, entity);
            return Result.success("更新表单成功");
        }
        return Result.fail(MsgCode.FA002.get());
    }

    /**
     * 发布流程引擎
     *
     * @param id 主键值
     * @return
     */
    @Operation("发布流程设计")
    @PostMapping("/Release/{id}")
    public Result release(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(1);
            flowEngineService.update(id, entity);
            return Result.success(MsgCode.SU011.get());
        }
        return Result.fail(MsgCode.FA011.get());
    }

    /**
     * 停止流程引擎
     *
     * @param id 主键值
     * @return
     */
    @Operation("停止流程设计")
    @PostMapping("/Stop/{id}")
    public Result stop(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(0);
            flowEngineService.update(id, entity);
            return Result.success(MsgCode.SU008.get());
        }
        return Result.fail(MsgCode.FA008.get());
    }

    /**
     * 工作流导出
     *
     * @param id 主键值
     * @return
     * @throws WorkFlowException
     */
    @Operation("工作流导出")
    @GetMapping("/{id}/Actions/ExportData")
    public Result exportData(@PathVariable("id") String id) throws WorkFlowException {
        FlowExportModel model = flowEngineService.exportData(id);
        DownloadVO downloadVO = fileExport.exportFile(model, configValueUtil.getTemporaryFilePath(), model.getFlowEngine().getFullName(), ModuleTypeEnum.FLOW_FLOWENGINE.getTableName());
        return Result.success(downloadVO);
    }

    /**
     * 工作流导入
     *
     * @param multipartFile 文件
     * @return
     * @throws WorkFlowException
     */
    @Operation("工作流导入")
    @PostMapping(value = "/Actions/ImportData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result ImportData(@RequestPart("file") MultipartFile multipartFile) throws WorkFlowException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.FLOW_FLOWENGINE.getTableName())) {
            return Result.fail(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile, configValueUtil.getTemporaryFilePath());
        FlowExportModel vo = JsonUtil.getJsonToBean(fileContent, FlowExportModel.class);
        return flowEngineService.ImportData(vo.getFlowEngine(), vo.getVisibleList());
    }

}
