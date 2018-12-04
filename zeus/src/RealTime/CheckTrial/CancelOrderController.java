package RealTime.CheckTrial;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.worksap.company.framework.forneus.ForneusViewId;
import com.worksap.company.framework.forneus.ForneusViewIds;
import com.worksap.company.framework.forneus.generator.handler.vo.WapAnchorLabelVo;
import com.worksap.company.framework.inputfw.controller.ApplicationPageSupport;
import com.worksap.company.framework.inputfw.controller.ReplaceTargetAreaId;
import com.worksap.company.framework.inputfw.controller.TemplateConst;
import com.worksap.company.framework.inputfw.model.ViewVo;
import com.worksap.company.framework.inputfw.model.application.Application;
import com.worksap.company.framework.inputfw.model.application.ApplicationEntity;
import com.worksap.company.framework.inputfw.model.application.ApplicationId;
import com.worksap.company.framework.inputfw.model.application.ApplicationStatus;
import com.worksap.company.framework.inputfw.model.application.ApplicationTransitionEntity;
import com.worksap.company.framework.inputfw.model.vo.ApplicationClassDefinition;
import com.worksap.company.framework.inputfw.model.vo.UserContextVo;
import com.worksap.company.framework.inputfw.module.InputPageSupportService;
import com.worksap.company.framework.inputfw.module.application.ApplicationReferenceService;
import com.worksap.company.framework.inputfw.module.autonumbering.ApplicaitnDataSourceWrapper;
import com.worksap.company.framework.inputfw.module.autonumbering.AutoNumberingRuleService;
import com.worksap.company.framework.inputfw.module.cache.ServiceSessionCacheSharedService;
import com.worksap.company.framework.inputfw.module.message.annotations.ApplicationInputStepEnum;
import com.worksap.company.framework.inputfw.module.message.annotations.ApplicationStep;
import com.worksap.company.framework.inputfw.module.view.ControllerHelper;
import com.worksap.company.framework.inputfw.view.ViewHelper;
import com.worksap.company.framework.inputfw.view.application.UpdateIdsResolver;
import com.worksap.company.framework.inputfw.view.transition.FirstPageTransition;
import com.worksap.company.framework.item.validation.BusinessValidator;
import com.worksap.company.framework.security.authority.AuthorityManager;
import com.worksap.company.framework.security.core.session.ServiceSessionCacheManager;
import com.worksap.company.framework.security.core.session.UserContext;
import com.worksap.company.framework.service.ServiceDefId;
import com.worksap.company.framework.service.ServiceId;
import com.worksap.company.framework.service.ServiceManager;
import com.worksap.company.framework.textresource.TextId;
import com.worksap.company.framework.textresource.TextMap;
import com.worksap.company.framework.textresource.TextResourceManager;
import com.worksap.company.framework.web.controller.ForneusViewController;
import com.worksap.company.framework.web.spring.mvc.PartialUpdateResponse;
import com.worksap.company.framework.web.spring.mvc.Response;
import com.worksap.company.hue.autonumbering.exception.NumberGeneratingException;
import com.worksap.company.hue.autonumbering.exception.RuleNotFoundException;
import com.worksap.company.hue.autonumbering.generator.DynamicDataGetter;
import com.worksap.company.hue.huedrive.webapi.client.HueDriveFileClient;
import com.worksap.company.hue.huedrive.webapi.spec.entity.FileEntityPojo;
import com.worksap.company.hue.huedrive.webapi.spec.entity.FileIDPojo;
import com.worksap.company.hue.scm.biz.com.context.ScmUserContext;
import com.worksap.company.hue.scm.biz.sales.inputfwimpl.ordersreceivedmanagement.ordersreceivedmanagement.contentsproviderimpl.OrderEntryContents;
import com.worksap.company.hue.scm.biz.sales.inputfwimpl.ordersreceivedmanagement.ordersreceivedmanagement.validator.SalesOrderCancelValidator;
import com.worksap.company.hue.scm.biz.sales.service.ordersreceivedmanagement.ordersreceivedmanagement.orderentry.SalesOrderService;
import com.worksap.company.hue.scm.biz.sales.util.CommonFunctionUtils;
import com.worksap.company.hue.scm.bizcore.sales.entity.SalesOrderAppHstEntity;
import com.worksap.company.hue.scm.bizcore.sales.entity.SalesOrderEntity;
import com.worksap.company.hue.scm.bizcore.sales.ifx.ie.ordersreceivedmanagement.ordersreceivedmanagement.utils.ItemArrangementStatusEnum;
import com.worksap.company.hue.scm.bizcore.sales.ifx.ie.ordersreceivedmanagement.ordersreceivedmanagement.utils.SalesOrderApplicationApprovalStatusEnum;
import com.worksap.company.hue.scm.bizcore.sales.ifx.ie.ordersreceivedmanagement.ordersreceivedmanagement.utils.SalesOrderApplicationStatusEnum;
import com.worksap.company.hue.scm.type.sales.SalesOrderDiscountSlip;
import com.worksap.company.hue.scm.type.sales.SalesOrderFileUploader;
import com.worksap.company.hue.scm.type.sales.SalesOrderReceivedDetailLocal;
import com.worksap.company.hue.scm.type.sales.SalesOrderReceivedHeader;
import com.worksap.company.hue.scm.type.sales.SalesOrderSlipDiscount;
import com.worksap.company.hue.user.converter.UserVoBuilder;
import com.worksap.company.hue.vo.ivtl.IvtlAnchorListItemVo;
import com.worksap.company.hue.vo.ivtl.IvtlAnchorListItemsVo;
import com.worksap.company.hue.vo.ivtl.IvtlFileAttachmentListVo;
import com.worksap.company.hue.vo.ivtl.IvtlFileAttachmentListVo.IvtlFileAttachmentListItem;
import com.worksap.company.hue.vo.ivtl.IvtlFinishMessageVo;

/**
 * CancelOrderController class is used for registering cancel application.
 *
 * @author Yuma Yoshikawa
 * @since HUE 17.09
 */
@Slf4j
@RequestMapping("/hue/scm/sales/ordersreceivedmanagement/ordercancel")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CancelOrderController {

    /** The user context. */

    private final UserContext userContext;

    /** The class definition. */
    private static final ApplicationClassDefinition<OrderEntryContents, ATE> CLASS_DEFINITION = ApplicationClassDefinition
            .of(OrderEntryContents.class, ATE.class);

    /** The lines salesOrderValidator. */
    @SalesOrderCancelValidator
    private final BusinessValidator<OrderEntryContents> salesOrderCancelValidator;

    /** The text Resource. */
    private final TextResourceManager textResource;

    /** The sales order service. */
    private final SalesOrderService salesOrderService;

    /** The file client. */
    private final HueDriveFileClient hueDriveFileClient;

    /** The service session cache shared service. */
    private final ServiceSessionCacheSharedService serviceSessionCacheSharedService;

    /** The service session cache shared service. */
    private final ServiceSessionCacheManager serviceSessionCacheManager;

    /** The application reference service. */
    private final ApplicationReferenceService<OrderEntryContents> applicationReferenceService;

    /** The auto numbering rule service. */
    private final AutoNumberingRuleService<OrderEntryContents> autoNumberingRuleService;

    /** The dynamic data getter. */
    @Qualifier("orderEntryDynamicDataGetter")
    private final DynamicDataGetter<ApplicaitnDataSourceWrapper<OrderEntryContents>> dynamicDataGetter;

    /** The service manager. */
    private final ServiceManager serviceManager;

    private final InputPageSupportService<OrderEntryContents, ATE> inputSupportService;

    /** The text resource manager. */
    private final TextResourceManager textResourceManager;

    /** The scm user context. */
    private final ScmUserContext scmUserContext;

    /** The authority manager. */
    private final AuthorityManager authorityManager;

    /** The UserVoBuilder. */
    private final UserVoBuilder userVoBuilder;

    /** The Constant CANCEL_ORDER_KEY. */
    private static final String CANCEL_ORDER_KEY = "cancelOrderKey";

    /** The Constant COPY_ORDER_KEY. */
    private static final String COPY_ORDER_KEY = "copyOrderKey";

    /** The DETAIL_VIEW_KEY. */
    private static final String DETAIL_VIEW_KEY = "detailViewKey";

    /** The Constant INITIAL. */
    private static final int INITIAL = 0;

    /** The Constant FINAL_VALUE. */
    private static final int FINAL_VALUE = 1;

    /** The Constant STATUS_NEGATIVE_VALUE. */
    private static final int STATUS_NEGATIVE_VALUE = -1;

    /** The Constant IVTL_FILE_ATTACHMENT. */
    private static final String IVTL_FILE_ATTACHMENT = "ivtlFileAttachment";

    /** The Constant ORDER_CANCEL_FRAMESET_INPUT_SCREEN. */
    private static final ForneusViewId ORDER_CANCEL_FRAMESET_INPUT_SCREEN = ForneusViewIds
            .withParent(OrderCancelConstants.ORDER_CANCEL_FRAMESET_PATH)
            .withChild(ReplaceTargetAreaId.WINDOW.getElementId(),
                    OrderCancelConstants.ORDER_CANCEL_FRAMESET_INPUT_PATH);

    /** The Constant ORDER_CANCEL_FRAMESET_CONFIRM_SCREEN. */
    private static final ForneusViewId ORDER_CANCEL_FRAMESET_CONFIRM_SCREEN = ForneusViewIds
            .withParent(OrderCancelConstants.ORDER_CANCEL_FRAMESET_PATH)
            .withChild(ReplaceTargetAreaId.WINDOW.getElementId(),
                    OrderCancelConstants.ORDER_CANCEL_FRAMESET_CONFIRM_PATH);

    /** The Constant ORDER_CANCEL_FRAMESET_FINISH_SCREEN. */
    private static final ForneusViewId ORDER_CANCEL_FRAMESET_FINISH_SCREEN = ForneusViewIds
            .withParent(OrderCancelConstants.ORDER_CANCEL_FRAMESET_PATH)
            .withChild(ReplaceTargetAreaId.WINDOW.getElementId(),
                    OrderCancelConstants.ORDER_CANCEL_FRAMESET_FINISH_PATH);

    /** The Constant REVISION_NUMBER_INCREMENT_VALUE. */
    private static final long REVISION_NUMBER_INCREMENT_VALUE = 1;

    /** The Constant SALES_ORDER_APPLICATION_ID. */
    private static final String SALES_ORDER_APPLICATION_ID = "salesOrderApplicationId";

    /**
     * The Class ATE.
     */
    @Data
    public static final class ATE extends
            ApplicationTransitionEntity<OrderEntryContents> {

        /** The internal document anchor list items. */
        IvtlAnchorListItemsVo internalDocumentAnchorListItems;

        /** The sample finish message. */
        private IvtlFinishMessageVo registerFinishMessage;

        /** The item file attachment list. */
        IvtlFileAttachmentListVo itemFileAttachmentList;

        @Override
        public Boolean getUseApprovalComment() {
            return true;
        }

        /**
         * Gets the internal document anchor list items.
         *
         * @return the internal document anchor list items
         */
        public IvtlAnchorListItemsVo getInternalDocumentAnchorListItems() {
            log.info(CommonFunctionUtils.getMethodName(this,
                    OrderCancelConstants.LOG_START));
            log.info(CommonFunctionUtils.getMethodName(this,
                    OrderCancelConstants.LOG_END));
            return this.internalDocumentAnchorListItems;
        }

        /**
         * Sets the internal document anchor list items.
         *
         * @param internalDocumentAnchorListItems
         *            the new internal document anchor list items
         */
        public void setInternalDocumentAnchorListItems(
                IvtlAnchorListItemsVo internalDocumentAnchorListItems) {
            log.info(CommonFunctionUtils.getMethodName(this,
                    OrderCancelConstants.LOG_START));
            this.internalDocumentAnchorListItems = internalDocumentAnchorListItems;
            log.info(CommonFunctionUtils.getMethodName(this,
                    OrderCancelConstants.LOG_END));
        }

        public void setItemFileAttachmentList(
                IvtlFileAttachmentListVo itemFileAttachmentList) {
            log.info(CommonFunctionUtils.getMethodName(this,
                    OrderCancelConstants.LOG_START));
            this.itemFileAttachmentList = itemFileAttachmentList;
            log.info(CommonFunctionUtils.getMethodName(this,
                    OrderCancelConstants.LOG_END));

        }
    };

    /**
     * Gets the proper forneus view id.
     *
     * @param invoiceLayoutKey
     *            the invoice layout key
     * @param suffixTemplate
     *            the suffix template
     * @param userContextVo
     *            the user context vo
     * @param isPartial
     *            the is partial
     * @return the proper forneus view id
     */
    private ForneusViewId getProperForneusViewId(String invoiceLayoutKey,
            String suffixTemplate, UserContextVo userContextVo,
            boolean isPartial) {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));
        ForneusViewId forneusViewId = null;
        if (TemplateConst.INPUT_SUFFIX.equals(suffixTemplate)) {
            forneusViewId = isPartial ? getPartialUpdateViewId(OrderCancelConstants.INPUT_VIEW_CONFIG_KEY)
                    : getViewId(OrderCancelConstants.ORDER_CANCEL_FRAMESET_INPUT_KEY);
        } else if (TemplateConst.CONFIRM_SUFFIX.equals(suffixTemplate)) {
            forneusViewId = getPartialUpdateViewId(OrderCancelConstants.ORDER_CANCEL_FRAMESET_CONFIRM_KEY);
        }
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
        return forneusViewId;
    }

    /**
     * Start.
     *
     * @return the response
     */
    @Override
    @RequestMapping(method = RequestMethod.GET)
    public Response start() {
        UserContextVo contextVo = UserContextVo.valueOf(userContext);
        ApplicationId applicationId = ApplicationId.getNewInstance();
        return inputSupportService
                .forwardFirstPage(OrderCancelConstants.FIRST_PAGE_TOKEN,
                        contextVo, applicationId, Maps.newHashMap(),
                        ApplicationStatus.START, null);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Response cancelStart(
            @RequestParam(required = false) String salesOrderApplicationId) {
        Map<String, List<String>> sessionMap = serviceSessionCacheSharedService
                .get(CANCEL_ORDER_KEY,
                        HashMap.class);
        UserContextVo contextVo = UserContextVo.valueOf(userContext);
        ApplicationId currentId = ApplicationId.valueOf(salesOrderApplicationId);
        Map<String, Object> param = Maps.newHashMap();
        List<String> originalAppIdList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(salesOrderApplicationId)) {
            originalAppIdList.add(salesOrderApplicationId);
        }
        param.put(SALES_ORDER_APPLICATION_ID, salesOrderApplicationId);
        Response response = inputSupportService.forwardFirstPage(
                OrderCancelConstants.FIRST_PAGE_TOKEN, contextVo, currentId,
                param, ApplicationStatus.START, null);
        return response;
    }

    private ViewVo<ATE> viewVo(ForneusViewId partialUpdateViewId,
            ApplicationInputStepEnum stepEnum) {
        List<String> updateIds = new ArrayList<>();
        updateIds.addAll(UpdateIdsResolver.getUpdateIdsByStep(stepEnum));
        return new ViewVo<>(partialUpdateViewId, updateIds,
                getApplicationTransitionEntityClass());
    }

    private Class<ATE> getApplicationTransitionEntityClass() {
        return ATE.class;
    }

    /**
     * Input.
     *
     * @return the response
     * @throws ParseException
     *             the parse exception
     * @throws NumberGeneratingException
     *             the number generating exception
     * @throws RuleNotFoundException
     *             the rule not found exception
     */
    @RequestMapping(method = RequestMethod.GET)
    public Response input(
            @RequestParam(required = false) String appId,
            @RequestParam(value = "appStatus", required = false) ApplicationStatus appStatus,
            @RequestParam(value = "otherAppId", required = false) String otherAppId,
            @RequestParam(value = "data-lazy", required = false) String dataLazy,
            @RequestParam(value = "salesOrderApplicationId", required = false) String salesOrderApplicationId) {
        Map<String, Object> restoreSessionMap = serviceSessionCacheManager.get(
                DETAIL_VIEW_KEY, HashMap.class);
        Map<String, List<String>> sessionMap = serviceSessionCacheSharedService
                .get(OrderCancelConstants.CANCEL_ORDER_KEY, HashMap.class);
        serviceSessionCacheSharedService.put(
                CANCEL_ORDER_KEY, sessionMap);
        FirstPageTransition<ATE> firstPageTransition;
        UserContextVo contextVo = UserContextVo.valueOf(userContext);
        ApplicationId otherApplicationId = Objects.isNull(otherAppId) ? null
                : ApplicationId.valueOf(otherAppId);
        firstPageTransition = this.inputSupportService
                .startPage(
                        getViewVo(ORDER_CANCEL_FRAMESET_INPUT_SCREEN,
                                ApplicationInputStepEnum.FIRST_PAGE),
                        contextVo, CLASS_DEFINITION,
                        ApplicationId.valueOf(appId), appStatus,
                        otherApplicationId)
                .prepare(
                        ate -> {
                            OrderEntryContents ordercancelcontents = setBusinessData(ate.getBusinessData(),
                                    salesOrderApplicationId);
                            List<SalesOrderFileUploader> attachedList = salesOrderService.getEntity(appId)
                                    .getSalesOrderFileUploaderList();
                            serviceSessionCacheManager.put(IVTL_FILE_ATTACHMENT, attachedList);
                            serviceSessionCacheManager.put(appId, attachedList);
                            if (Objects.isNull(attachedList)) {
                                attachedList = new ArrayList<SalesOrderFileUploader>();
                            }
                            IvtlAnchorListItemsVo ivtlAnchorListItemsVo = getAnchorListItemsVo(ate
                                    .getBusinessData()
                                    .getSalesOrderFileUploaderList());
                            ate.setInternalDocumentAnchorListItems(ivtlAnchorListItemsVo);
                            ate.setBusinessData(ordercancelcontents);
                            this.serviceSessionCacheManager.put(
                                    OrderCancelConstants.ORDER_RECEIVED_ID,
                                    ordercancelcontents.getOrderReceivedId());
                        });
        Response response = firstPageTransition.transit();
        serviceSessionCacheManager.put(DETAIL_VIEW_KEY, restoreSessionMap);
        return response;
    }

    private IvtlFileAttachmentListVo getFileUploadListItemsVo(
            List<SalesOrderFileUploader> salesOrderFileUploaderList) {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));
        List<IvtlFileAttachmentListItem> ivtlFileAttachmentListItem = new ArrayList<>();
        IvtlFileAttachmentListVo list = IvtlFileAttachmentListVo.builder()
                .items(ivtlFileAttachmentListItem).build();
        if (Objects.nonNull(salesOrderFileUploaderList)) {
            salesOrderFileUploaderList.stream().forEach(
                    item -> {
                        ivtlFileAttachmentListItem
                                .add(IvtlFileAttachmentListItem.builder()
                                        .name(item.getFileName())
                                        .id(item.getFileId())
                                        .size(item.getFileSize()).build());
                    });
            list = IvtlFileAttachmentListVo.builder()
                    .items(ivtlFileAttachmentListItem).build();
        }
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
        return list;

    }

    private OrderEntryContents setBusinessData(OrderEntryContents contents,
            String salesOrderApplicationId) {
        SalesOrderEntity salesOrderEntity = salesOrderService
                .getEntity(salesOrderApplicationId);
        TextMap textMap = textResourceManager.getTexts(Arrays.asList(TextId.of("SALE.SORM.discountNameLabel"),
                TextId.of("SALE.SORM.discountRateLabel"), TextId.of("SALE.SORM.discountAmountLabel")));
        UserContextVo currentUserContextVo = UserContextVo.valueOf(userContext);
        SalesOrderReceivedHeader salesOrderReceivedHeader = new SalesOrderReceivedHeader();
        List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList = new ArrayList<SalesOrderReceivedDetailLocal>();
        AtomicInteger rowIndex = new AtomicInteger();
        contents.setOrderReceivedId(salesOrderEntity.getOrderReceivedId());
        contents.setSalesOrderApplicationId(salesOrderEntity.getSalesOrderApplicationId());
        contents.setSalesOrderApplicationNumber(salesOrderEntity
                .getSalesOrderApplicationNumber());
        contents.setSalesOrderSlipNumber(salesOrderEntity.getSalesOrderSlipNumber());
        contents.setSalesOrderDate(salesOrderEntity.getSalesOrderDate());
        contents.setSalesCustomerName(salesOrderEntity.getSalesCustomerName());
        salesOrderReceivedHeader = salesOrderEntity.getSalesOrderReceivedHeader();
        contents.setSalesOrderReceivedHeader(salesOrderReceivedHeader);
        salesOrderReceivedDetailList = salesOrderEntity.getSalesOrderReceivedDetailLocalList();
        salesOrderReceivedDetailList = salesOrderService
                .removeEmptyDetails(salesOrderReceivedDetailList);
        SalesOrderSlipDiscount salesOrderSlipDiscount = salesOrderService.getRevisionNumber(salesOrderEntity
                .getSalesOrderApplicationId());
        if (Objects.nonNull(salesOrderSlipDiscount)) {
            SalesOrderDiscountSlip salesOrderDiscountSlip = new SalesOrderDiscountSlip();
            List<SalesOrderDiscountSlip> salesOrderDiscountSlipList = new ArrayList<>();
            salesOrderDiscountSlip.setDiscountDetailType(salesOrderSlipDiscount.getDiscountCalculationType());
            salesOrderDiscountSlip.setDiscountAmount(salesOrderSlipDiscount.getTotalAmount());
            salesOrderDiscountSlip
                    .setDiscountLabel(textResourceManager.getText(TextId.of("SALE.SORM.slipTotalAmount")));
            salesOrderDiscountSlip.setTotalOrderAmount(salesOrderEntity.getSalesOrderReceivedDetailLocalList()
                    .get(INITIAL).getTotalAmount());
            salesOrderDiscountSlip.setDiscountNameLabel(textMap.get("SALE.SORM.discountNameLabel"));
            salesOrderDiscountSlip.setDiscountRateLabel(textMap.get("SALE.SORM.discountRateLabel"));
            salesOrderDiscountSlip.setDiscountAmountLabel(textMap.get("SALE.SORM.discountAmountLabel"));
            salesOrderDiscountSlip.setDiscountName(salesOrderSlipDiscount.getDiscountDocumentName());
            salesOrderDiscountSlip.setDiscountRate(salesOrderSlipDiscount.getDiscountRate());
            salesOrderDiscountSlipList.add(salesOrderDiscountSlip);
            contents.setSalesOrderDiscountSlipList(salesOrderDiscountSlipList);
            contents.setSlipDiscount(salesOrderSlipDiscount.getTotalAmount());
            contents.setSlipTotalAmount(salesOrderDiscountSlip.getTotalOrderAmount());
            SalesOrderReceivedHeader salesOrderReceivedUpdatedHeader = contents.getSalesOrderReceivedHeader();
            salesOrderReceivedUpdatedHeader.setTotalOrderAmount(salesOrderDiscountSlip.getTotalOrderAmount().add(
                    salesOrderSlipDiscount.getTotalAmount()));
            contents.setSalesOrderReceivedHeader(salesOrderReceivedUpdatedHeader);
        }
        salesOrderReceivedDetailList
                .stream()
                .forEach(
                        detail -> {
                            detail.setRowNumber(rowIndex.incrementAndGet());
                            detail.setItemArrangementStatus(ItemArrangementStatusEnum.NOT_ARRANGED
                                    .getStatusValue());
                        });
        contents.setSalesOrderReceivedDetailList(salesOrderReceivedDetailList);
        List<SalesOrderFileUploader> salesOrderFileUploaderEntityList = salesOrderEntity
                .getSalesOrderFileUploaderList();
        contents.setSalesOrderFileUploaderList(salesOrderFileUploaderEntityList);
        contents.setCorporationId(scmUserContext.getActiveCorpId());
        contents.setSalesRepresentative(salesOrderEntity.getSalesRepresentative());
        contents.setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.UPDATED
                .getStatusValue());
        contents.setServiceId(salesOrderEntity.getServiceId());
        return contents;
    }

    @Override
    public Response confirm(@RequestParam String appId) {
        UserContextVo contextVo = UserContextVo.valueOf(userContext);
        ATE applicationTransitionEntity = this.inputSupportService
                .getViewEntity(contextVo, CLASS_DEFINITION,
                        ApplicationId.valueOf(appId));
        return buildConfirmViewHelper().toResponse(
                applicationTransitionEntity);
    }

    /**
     * buildConfirmViewHelper method builds the confirm view helper.
     *
     * @return the view helper
     */
    private ViewHelper<ATE> buildConfirmViewHelper() {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
        return ControllerHelper.CONFIRM
                .<ATE> buildViewHelper(getViewVo(
                        getPartialUpdateViewId(OrderCancelConstants.CONFIRM_VIEW_CONFIG_KEY),
                        ApplicationInputStepEnum.CONFIRM));
    }

    /**
     * Gets the anchor list items vo.
     *
     * @param salesOrderFileUploaderList
     *            the sales order file uploader list
     * @return the anchor list items vo
     */
    private IvtlAnchorListItemsVo getAnchorListItemsVo(
            List<SalesOrderFileUploader> salesOrderFileUploaderList) {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));
        List<SalesOrderFileUploader> salesOrderFileUploaderValidList = Objects
                .nonNull(salesOrderFileUploaderList) ? salesOrderFileUploaderList
                : new ArrayList<SalesOrderFileUploader>();
        IvtlAnchorListItemsVo ivtlAnchorListItemsVo = new IvtlAnchorListItemsVo();
        List<IvtlAnchorListItemVo> ivtlAnchorListItemVoList = new ArrayList<IvtlAnchorListItemVo>();
        if (CollectionUtils.isNotEmpty(salesOrderFileUploaderValidList)) {
            salesOrderFileUploaderValidList.stream().forEach(
                    item -> {
                        ivtlAnchorListItemVoList
                                .add(new IvtlAnchorListItemVo.Builder().anchor(
                                        new WapAnchorLabelVo.Builder().label(
                                                item.getFileName()).build())
                                        .fileId(item.getFileId())
                                        .size(item.getFileSize())
                                        .userId(item.getFileUploaderUserId())
                                        .timeUUID(item.getTimeUUID())
                                        .build());
                    });
        }
        ivtlAnchorListItemsVo.setChildren(ivtlAnchorListItemVoList);
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
        return ivtlAnchorListItemsVo;

    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public PartialUpdateResponse confirm(
            @RequestParam String appId,
            @RequestBody ApplicationEntity<OrderEntryContents> applicationEntity) {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));

        UserContextVo userContextVo = UserContextVo.valueOf(userContext);
        PartialUpdateResponse response = this.inputSupportService
                .confirm(
                        userContextVo,
                        CLASS_DEFINITION,
                        ApplicationId.valueOf(appId),
                        buildConfirmViewHelper()
                                .toTransition(applicationEntity),
                        Arrays.asList(salesOrderCancelValidator))
                .prepare(
                        ate -> {
                            OrderEntryContents ateBusinessData = ate
                                    .getBusinessData();
                            List<SalesOrderFileUploader> salesOrderFileUploaderList = serviceSessionCacheManager
                                    .get(ate.getApplicationId().toString(),
                                            List.class);
                            ateBusinessData
                                    .setSalesOrderApplicationId("010101");
                            ateBusinessData
                                    .setSalesCustomerName(applicationEntity
                                            .getBusinessData()
                                            .getSalesCustomerName());
                            ateBusinessData
                                    .setSalesRepresentative(applicationEntity
                                            .getBusinessData()
                                            .getSalesRepresentative());
                            ateBusinessData
                                    .setSalesOrderReceivedHeader(applicationEntity
                                            .getBusinessData()
                                            .getSalesOrderReceivedHeader());
                            List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList = salesOrderService
                                    .removeEmptyDetails(ateBusinessData
                                            .getSalesOrderReceivedDetailList());
                            ateBusinessData
                                    .setSalesOrderReceivedDetailList(applicationEntity
                                            .getBusinessData()
                                            .getSalesOrderReceivedDetailList());
                            AtomicReference<BigDecimal> totalAmount = new AtomicReference<BigDecimal>();
                            totalAmount.set(BigDecimal.ZERO);
                            salesOrderReceivedDetailList
                                    .stream()
                                    .forEach(
                                            detail -> {
                                                if (Objects.nonNull(detail
                                                        .getTotalAmount())) {
                                                    totalAmount.set(totalAmount
                                                            .get()
                                                            .add(detail
                                                                    .getTotalAmount()));
                                                }
                                            });
                            ateBusinessData.getSalesOrderReceivedHeader()
                                    .setTotalOrderAmount(totalAmount.get());
                            OrderEntryContents info = ate.getBusinessData();
                            if (Objects.nonNull(salesOrderFileUploaderList)) {
                                info.setSalesOrderFileUploaderList(salesOrderFileUploaderList);
                            }
                            IvtlAnchorListItemsVo ivtlAnchorListItemsVo = getAnchorListItemsVo(ateBusinessData
                                    .getSalesOrderFileUploaderList());
                            ate.setInternalDocumentAnchorListItems(ivtlAnchorListItemsVo);
                        }).transitPartial();
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
        return response;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public Response finish(@RequestParam String appId) {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));
        UserContextVo contextVo = UserContextVo.valueOf(userContext);
        final ATE model = this.inputSupportService.getViewEntity(contextVo,
                CLASS_DEFINITION, ApplicationId.valueOf(appId));
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
        return buildFinishViewHelper().toResponse(model);
    }

    /**
     * buildFinishViewHelper method returns SALES_CONTRACT_REGISTRATION_CONFIG_FINISH_KEY to the finish method.
     *
     * @return FINISH_VIEW_CONFIG_KEY
     */
    private ViewHelper<ATE> buildFinishViewHelper() {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
        return ControllerHelper.FINISH
                .<ATE> buildViewHelper(getViewVo(
                        getPartialUpdateViewId(OrderCancelConstants.FINISH_VIEW_CONFIG_KEY),
                        ApplicationInputStepEnum.FINISH));
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public PartialUpdateResponse finish(@RequestParam String appId,
            @RequestBody OrderEntryContents applicationEntity) {
        log.info(CommonFunctionUtils.getMethodName(this, OrderCancelConstants.LOG_START));
        List<String> updatedSalesOrderApplicationId = new ArrayList<>();
        Map<String, List<String>> sessionMap = serviceSessionCacheManager.get(
                DETAIL_VIEW_KEY, HashMap.class);
        final UserContextVo contextVo = UserContextVo.valueOf(userContext);
        final ATE ate = this.inputSupportService.getViewEntity(contextVo,
                CLASS_DEFINITION, ApplicationId.valueOf(appId));
        final ApplicationId applicationId = ate.getApplicationId();
        final UUID orderReceivedId = this.serviceSessionCacheManager.get(
                OrderCancelConstants.ORDER_RECEIVED_ID, UUID.class);
        List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList = new ArrayList<SalesOrderReceivedDetailLocal>();
        AtomicInteger rowIndex = new AtomicInteger();
        SalesOrderAppHstEntity newSalesOrderAppHstEntity = salesOrderService
                .getAppHstEntity(orderReceivedId.toString());
        if (StringUtils.isNotEmpty(newSalesOrderAppHstEntity.getSalesOrderSlipNumber())) {
            String slipNumberKeyValue = newSalesOrderAppHstEntity.getSalesOrderSlipNumber();
            serviceSessionCacheManager.put("cancelSlipNumberKey", slipNumberKeyValue);
        }
        final PartialUpdateResponse response = this.inputSupportService.finish(
                contextVo, CLASS_DEFINITION, ApplicationId.valueOf(appId),
                buildFinishViewHelper().toTransition(ate), dynamicDataGetter,
                autoNumberingRuleService).transitPartial();
        SalesOrderAppHstEntity salesOrderAppHstEntityOpt = salesOrderService
                .getAppHstEntity(orderReceivedId.toString());
        if (Objects.isNull(salesOrderAppHstEntityOpt)) {
            throw new RuntimeException(
                    "SalesOrderAppHstEntity not found. orderReceivedId: "
                            + orderReceivedId);

        } else {
            SalesOrderAppHstEntity salesOrderAppHstEntity = salesOrderService
                    .getAppHstEntity(orderReceivedId.toString());
            final Application application = applicationReferenceService
                    .getApplication(contextVo, applicationId);
            salesOrderAppHstEntity.setSalesOrderApplicationId(appId);
            salesOrderAppHstEntity.setSalesOrderApplicationNumber(application
                    .getApplicationNumber());
            salesOrderReceivedDetailList = ate.getBusinessData()
                    .getSalesOrderReceivedDetailList();
            salesOrderReceivedDetailList = salesOrderService
                    .removeEmptyDetails(salesOrderReceivedDetailList);
            salesOrderReceivedDetailList
                    .stream()
                    .forEach(
                            detail -> {
                                detail.setRowNumber(rowIndex.incrementAndGet());
                                detail.setItemArrangementStatus(ItemArrangementStatusEnum.ARRANGEMENT_UNNECESSARY
                                        .getStatusValue());
                            });
            salesOrderAppHstEntity
                    .setSalesOrderReceivedDetail(salesOrderReceivedDetailList);
            salesOrderAppHstEntity
                    .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.CANCEL_SUBMITTED
                            .getStatusValue());
            salesOrderAppHstEntity
                    .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.NEW
                            .getStatusValue());
            salesOrderAppHstEntity.setUpdatedUser(userContext.getUser().getNameEn());
            salesOrderAppHstEntity.setUpdatedDate(ZonedDateTime.now());
            salesOrderAppHstEntity
                    .setServiceId(OrderCancelConstants.ORDER_ENTRY_CFG_ID);
            salesOrderAppHstEntity.setRevisionNumber(salesOrderAppHstEntityOpt.getRevisionNumber()
                    + REVISION_NUMBER_INCREMENT_VALUE);
            salesOrderAppHstEntityOpt.setLatestRevisionFlg(INITIAL);
            salesOrderAppHstEntity.setLatestRevisionFlg(FINAL_VALUE);
            List<SalesOrderDiscountSlip> salesOrderDiscountSlipList = ate.getBusinessData()
                    .getSalesOrderDiscountSlipList();
            if (CollectionUtils.isNotEmpty(salesOrderDiscountSlipList)) {
                SalesOrderSlipDiscount salesOrderSlipDiscount = new SalesOrderSlipDiscount();
                salesOrderSlipDiscount.setDiscountCalculationType(salesOrderDiscountSlipList.get(INITIAL)
                        .getDiscountDetailType());
                salesOrderSlipDiscount.setDiscountDocumentName(salesOrderDiscountSlipList.get(INITIAL)
                        .getDiscountName());
                salesOrderSlipDiscount.setDiscountRate(salesOrderDiscountSlipList.get(INITIAL).getDiscountRate());
                salesOrderSlipDiscount.setTotalAmount(salesOrderDiscountSlipList.get(INITIAL).getDiscountAmount());
                salesOrderAppHstEntity.setSalesOrderSlipDiscount(salesOrderSlipDiscount);
            }
            salesOrderService.insertSalesOrderAppHstEntity(salesOrderAppHstEntityOpt);
            salesOrderService.insertSalesOrderAppHstEntity(salesOrderAppHstEntity);
        }
        serviceSessionCacheManager.put(DETAIL_VIEW_KEY, sessionMap);
        log.info(CommonFunctionUtils.getMethodName(this, OrderCancelConstants.LOG_END));
        return response;

    }

    @ResponseBody
    public String getPortalServiceId() {
        Set<ServiceId> serviceIdsSet = authorityManager.getAccessibleServiceIds(scmUserContext.getActiveRoleIdList());
        List<ServiceId> accessibleServiceIdsList = new ArrayList<>();
        accessibleServiceIdsList.addAll(serviceManager
                .getServiceCfgsByServiceDefId(ServiceDefId.valueOf(OrderCancelConstants.PORTAL_SERVICE_DEF_ID))
                .stream()
                .filter(serviceCfg -> serviceIdsSet.contains(serviceCfg.getServiceId()))
                .map(serviceCfg -> serviceCfg.getServiceId())
                .collect(Collectors.toList()));
        return getJsonString(accessibleServiceIdsList.get(OrderCancelConstants.INITIAL_INDEX).toString());
    }

    /**
     * getJsonString is used to get the JSON values
     *
     * @param jsonObject
     *            the json object
     * @return parseValue the value parsed as string
     */
    private String getJsonString(Object jsonObject) {
        String parseValue = StringUtils.EMPTY;
        try {
            ObjectMapper mapper = new ObjectMapper();
            parseValue = mapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("JSON processing exception");
            throw new IllegalArgumentException(jsonProcessingException);
        }
        return parseValue;
    }

    @Override
    public Map<String, Object> onChange(
            ApplicationEntity<OrderEntryContents> model) {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));
        Map<String, Object> onChangeMap = inputSupportService.onChange(
                UserContextVo.valueOf(userContext), CLASS_DEFINITION, model,
                new ArrayList<>());
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
        return onChangeMap;
    }

    @Override
    public ZonedDateTime save(ApplicationEntity<OrderEntryContents> model) {
        log.info(CommonFunctionUtils.getMethodName(this, OrderCancelConstants.LOG_START));
        UserContextVo userContextVo = UserContextVo.valueOf(userContext);
        ATE ateToSave = inputSupportService.getViewEntity(userContextVo, CLASS_DEFINITION,
                ApplicationId.valueOf(model.getApplicationId()));
        ateToSave.setBusinessData(model.getBusinessData());
        inputSupportService.putViewEntity(userContextVo, ateToSave);
        inputSupportService.save(userContextVo, CLASS_DEFINITION, model, new ArrayList<>());
        log.info(CommonFunctionUtils.getMethodName(this, OrderCancelConstants.LOG_END));
        return null;
    }

    @Override
    public Response restart(String appId) {
        log.info(CommonFunctionUtils.getMethodName(this, OrderCancelConstants.LOG_START));
        UserContextVo userContextVo = UserContextVo.valueOf(userContext);
        ApplicationId applicationId = ApplicationId.valueOf(appId);
        Response response = null;
        response = inputSupportService.forwardFirstPage(OrderCancelConstants.FIRST_PAGE_TOKEN,
                userContextVo,
                applicationId,
                new HashMap<>(),
                ApplicationStatus.START, null);
        return response;
    }

    @Override
    public Response copy(String appId) {
        log.info(CommonFunctionUtils.getMethodName(this, OrderCancelConstants.LOG_START));
        UserContextVo userContextVo = UserContextVo.valueOf(userContext);
        log.info(CommonFunctionUtils.getMethodName(this, OrderCancelConstants.LOG_END));
        return inputSupportService.forwardFirstPage(
                OrderCancelConstants.FIRST_PAGE_TOKEN,
                userContextVo, ApplicationId.getNewInstance(),
                new HashMap<>(), ApplicationStatus.COPY, ApplicationId.valueOf(appId));
    }

    @Override
    public String getServiceDefId() {
        return OrderCancelConstants.SERVICE_DEF_ID;
    }

    @Override
    protected Map<String, ForneusViewId> buildAllForneusViewId() {
        Map<String, ForneusViewId> map = new HashMap<>();
        map.put(OrderCancelConstants.ORDER_CANCEL_FRAMESET_INPUT_KEY,
                ORDER_CANCEL_FRAMESET_INPUT_SCREEN);
        map.put(OrderCancelConstants.ORDER_CANCEL_FRAMESET_CONFIRM_KEY,
                ORDER_CANCEL_FRAMESET_CONFIRM_SCREEN);
        map.put(OrderCancelConstants.ORDER_CANCEL_FRAMESET_FINISH_KEY,
                ORDER_CANCEL_FRAMESET_FINISH_SCREEN);
        return map;
    }

    @Override
    protected void buildAllPartialUpdateViewConfig() {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));
        super.buildAllPartialUpdateViewConfig();
        this.addPartailUpdateViewConfig(
                OrderCancelConstants.INPUT_VIEW_CONFIG_KEY,
                OrderCancelConstants.ORDER_CANCEL_FRAMESET_INPUT_KEY,
                UpdateIdsResolver
                        .getUpdateIds(ApplicationInputStepEnum.MOVE_PAGE));
        this.addPartailUpdateViewConfig(
                OrderCancelConstants.CONFIRM_VIEW_CONFIG_KEY,
                OrderCancelConstants.ORDER_CANCEL_FRAMESET_CONFIRM_KEY,
                UpdateIdsResolver
                        .getUpdateIds(ApplicationInputStepEnum.CONFIRM));
        this.addPartailUpdateViewConfig(
                OrderCancelConstants.FINISH_VIEW_CONFIG_KEY,
                OrderCancelConstants.ORDER_CANCEL_FRAMESET_FINISH_KEY,
                UpdateIdsResolver.getUpdateIds(ApplicationInputStepEnum.FINISH));
        this.addPartailUpdateViewConfig(OrderCancelConstants.BINDER_CARD_GRID,
                OrderCancelConstants.ORDER_CANCEL_FRAMESET_INPUT_KEY,
                OrderCancelConstants.BINDER_CARD_GRID);
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
    }

    /***
     * Utils
     */

    /**
     * buildConfirmViewHelper.
     *
     * @return the view helper
     */
    private ViewHelper<ATE> buildViewHelper(ControllerHelper step,
            String viewKey, ApplicationInputStepEnum stepEnum) {
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_START));
        ViewHelper<ATE> viewHelper = step.<ATE> buildViewHelper(viewVo(
                getPartialUpdateViewId(viewKey), stepEnum));
        log.info(CommonFunctionUtils.getMethodName(this,
                OrderCancelConstants.LOG_END));
        return viewHelper;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ViewVo<ATE> getViewVo(ForneusViewId forneusViewId,
            ApplicationStep step) {
        List<String> updateIds = new ArrayList<>();
        updateIds.add(TemplateConst.PARTIAL_TARGET_COMMENT_CONTAINER);
        updateIds.addAll(UpdateIdsResolver.getUpdateIdsByStep(step));
        return new ViewVo<>(forneusViewId, updateIds, ATE.class);
    }

    /**
     * fetchPreview preview file from hue drive.
     *
     * @param fileId
     *            the file id.
     * @param timeUUID
     *            the time uuid.
     * @param request
     *            the request.
     * @param response
     *            the response.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD })
    public void fetchPreview(@RequestParam String fileId, @RequestParam String timeUUID,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String filePreviewId = StringUtils
                .substringBefore(fileId, ":");
        executeHueDriveGetPreview(filePreviewId, UUID.fromString(timeUUID), userContext.getSessionToken(), response,
                request);
    }

    /**
     * executeHueDriveGetPreview is to preview the file.
     *
     * @param fileId
     *            the file id.
     * @param timeUUID
     *            the time uuid.
     * @param userId
     *            the user id.
     * @param response
     *            the response.
     * @param request
     *            the request.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void executeHueDriveGetPreview(String fileId, UUID timeUUID, String userId,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        FileIDPojo postVo = FileIDPojo.builder().fileId(fileId).timeUUID(timeUUID.toString()).userId(userId).build();
        try {
            hueDriveFileClient.getPreview(postVo, request, response, userContext.getSessionToken());
        } catch (JsonProcessingException exception) {
            log.error(exception.getMessage());
            throw new IllegalArgumentException(exception);
        }
    }

    /**
     * download is used to download the pdf.
     *
     * @param fileId
     *            the file id
     * @param timeUUID
     *            the time uuid
     * @param userId
     *            the user id
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public void download(@RequestParam String fileId, @RequestParam String timeUUID, @RequestParam String userId,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String agent = request.getHeader("user-agent");
        String filePreviewId = StringUtils
                .substringBefore(fileId, ":");
        InputStream result = hueDriveFileClient.download(filePreviewId, userContext.getSessionToken());
        FileEntityPojo fileEntity = hueDriveFileClient.loadFile(FileIDPojo.builder().fileId(filePreviewId)
                .timeUUID(timeUUID).userId(userId).build(), userContext.getSessionToken());
        String encodedFileName = encodeFileName(agent, fileEntity.getName());
        response.setHeader("Content-Disposition",
                new StringBuilder().append("attachment; filename=\"").append(encodedFileName)
                        .append("\"").toString());
        IOUtils.copy(result, response.getOutputStream());
    }

    /**
     * encodeFileName encodes file name.
     *
     * @param agent
     *            the agent
     * @param fileName
     *            the file name
     * @return the string
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     */
    private String encodeFileName(String agent, String fileName) throws UnsupportedEncodingException {
        String encodedFileName;
        if (!checkIsIE(agent)) {
            encodedFileName = new String(fileName.getBytes("UTF-8"),
                    "ISO_8859_1");
        } else {
            encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace(
                    "+", "%20");
        }
        return encodedFileName.replaceAll("[\\\\/]", "-");
    }

    /**
     * checkIsIE checks is ie.
     *
     * @param userAgent
     *            the user agent
     * @return true, if successful
     */
    private boolean checkIsIE(String userAgent) {
        if (StringUtils.isEmpty(userAgent)) {
            return false;
        }
        return (userAgent.indexOf("MSIE") != STATUS_NEGATIVE_VALUE)
                || (userAgent.indexOf("Edge") != STATUS_NEGATIVE_VALUE)
                || ((userAgent.indexOf("rv:") != STATUS_NEGATIVE_VALUE) && userAgent
                        .indexOf("Trident/") != STATUS_NEGATIVE_VALUE);
    }

}
