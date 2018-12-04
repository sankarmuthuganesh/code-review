//package com.worksap.company.hue.scm.biz.sales.service.ordersreceivedmanagement.ordersreceivedmanagement.orderentry;
//
//import java.math.BigDecimal;
//import java.math.MathContext;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
//import lombok.RequiredArgsConstructor;
//
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.worksap.company.framework.elasticsearch.basic.autocomplete.AutocompleteEntity;
//import com.worksap.company.framework.elasticsearch.basic.autocomplete.AutocompleteResult;
//import com.worksap.company.framework.elasticsearch.basic.autocomplete.AutocompleteTypeSetting;
//import com.worksap.company.framework.elasticsearch.basic.autocomplete.Autocompleter;
//import com.worksap.company.framework.elasticsearch.basic.autocomplete.AutocompleterFactory;
//import com.worksap.company.framework.elasticsearch.mapper.FullTextSearchResult;
//import com.worksap.company.framework.elasticsearch.mapper.HitDocument;
//import com.worksap.company.framework.elasticsearch.mapper.HitDocumentResponseMapper;
//import com.worksap.company.framework.forneus.generator.handler.util.CommonDataAttributeType.CommonIconType;
//import com.worksap.company.framework.forneus.generator.handler.vo.WapAnchorLabelVo;
//import com.worksap.company.framework.forneus.generator.handler.vo.WapTextLabelVo;
//import com.worksap.company.framework.inputfw.item.ResponseItemMap;
//import com.worksap.company.framework.inputfw.model.application.ApplicationEntity;
//import com.worksap.company.framework.security.authority.AuthorityManager;
//import com.worksap.company.framework.security.core.session.UserContext;
//import com.worksap.company.framework.service.ServiceDefId;
//import com.worksap.company.framework.service.ServiceId;
//import com.worksap.company.framework.service.ServiceManager;
//import com.worksap.company.framework.textresource.TextId;
//import com.worksap.company.framework.textresource.TextMap;
//import com.worksap.company.framework.textresource.TextResourceManager;
//import com.worksap.company.hue.approvalflow.webapi.spec.enumeration.FlowOperationType;
//import com.worksap.company.hue.approvalflow.webapi.spec.id.ApplicationId;
//import com.worksap.company.hue.approvalflow.webapi.spec.vo.event.FlowEventListenerParameterVo;
//import com.worksap.company.hue.com.type.ac.fe.ForeignExchangeResult;
//import com.worksap.company.hue.scm.biz.com.context.ScmUserContext;
//import com.worksap.company.hue.scm.biz.com.service.SalesDeliveryDestinationMasterService;
//import com.worksap.company.hue.scm.biz.sales.entity.ordersreceivedmanagement.ordersreceivedmanagement.SalesIvtlFileDelete;
//import com.worksap.company.hue.scm.biz.sales.enums.ordersreceivedmanagement.ordersreceivedmanagement.DiscountCalculationType;
//import com.worksap.company.hue.scm.biz.sales.enums.ordersreceivedmanagement.ordersreceivedmanagement.DiscountDetailType;
//import com.worksap.company.hue.scm.biz.sales.inputfwimpl.ordersreceivedmanagement.ordersreceivedmanagement.contentsproviderimpl.OrderEntryContents;
//import com.worksap.company.hue.scm.biz.sales.vo.ordersreceivedmanagement.ordersreceivedmanagement.DeliveryDestinationVo;
//import com.worksap.company.hue.scm.bizcore.com.entity.SalesDeliveryDestinationMasterEntity;
//import com.worksap.company.hue.scm.bizcore.com.tax.TaxCalcException;
//import com.worksap.company.hue.scm.bizcore.com.tax.TaxCalculationEntity;
//import com.worksap.company.hue.scm.bizcore.com.tax.TaxCalculationMode;
//import com.worksap.company.hue.scm.bizcore.com.tax.TaxCalculatorCoreService;
//import com.worksap.company.hue.scm.bizcore.sales.dao.SalesOrderAppHstDao;
//import com.worksap.company.hue.scm.bizcore.sales.dao.SalesOrderDao;
//import com.worksap.company.hue.scm.bizcore.sales.entity.SalesOrderAppHstEntity;
//import com.worksap.company.hue.scm.bizcore.sales.entity.SalesOrderAppHstIndexEntity;
//import com.worksap.company.hue.scm.bizcore.sales.entity.SalesOrderEntity;
//import com.worksap.company.hue.scm.bizcore.sales.ifx.ie.ordersreceivedmanagement.ordersreceivedmanagement.utils.ItemArrangementStatusEnum;
//import com.worksap.company.hue.scm.bizcore.sales.ifx.ie.ordersreceivedmanagement.ordersreceivedmanagement.utils.SalesOrderApplicationApprovalStatusEnum;
//import com.worksap.company.hue.scm.bizcore.sales.ifx.ie.ordersreceivedmanagement.ordersreceivedmanagement.utils.SalesOrderApplicationStatusEnum;
//import com.worksap.company.hue.scm.type.sales.SalesOrderDiscount;
//import com.worksap.company.hue.scm.type.sales.SalesOrderDiscountSlip;
//import com.worksap.company.hue.scm.type.sales.SalesOrderFileUploader;
//import com.worksap.company.hue.scm.type.sales.SalesOrderReceivedDetailLocal;
//import com.worksap.company.hue.scm.type.sales.SalesOrderReceivedHeader;
//import com.worksap.company.hue.scm.type.sales.SalesOrderSlipDiscount;
//import com.worksap.company.hue.scm.type.sales.SalesOrderSubtotal;
//import com.worksap.company.hue.scm.type.sales.TargetDetail;
//import com.worksap.company.hue.vo.ivtl.IvtlAnchorListItemVo;
//import com.worksap.company.hue.vo.ivtl.IvtlAnchorListItemsVo;
//import com.worksap.company.hue.vo.ivtl.IvtlFileAttachmentListVo;
//import com.worksap.company.hue.vo.ivtl.IvtlFileAttachmentListVo.IvtlFileAttachmentListItem;
//
///**
// * SalesOrderServiceImpl implements SalesOrderService and has all the business logic of the entry screen.
// *
// * @author Ajith.R
// * @since HUE 17.06
// */
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class SalesOrderServiceImpl implements SalesOrderService {
//
//    /** The sales order dao. */
//    private final SalesOrderDao salesOrderDao;
//
//    /** The salesOrderAppHstBaseDao. */
//    private final SalesOrderAppHstDao salesOrderAppHstDao;
//
//    /** The scm user context. */
//    private final ScmUserContext scmUserContext;
//
//    /** The taxCalculatorCoreService. */
//    private final TaxCalculatorCoreService taxCalculatorCoreService;
//
//    /** The salesDeliveryDestinationMasterService. */
//    private final SalesDeliveryDestinationMasterService salesDeliveryDestinationMasterService;
//
//    /** The user context. */
//    private final UserContext userContext;
//
//    /** The autocompleterFactory. */
//    private final AutocompleterFactory autocompleterFactory;
//
//    /** The text resource manager. */
//    private final TextResourceManager textResourceManager;
//
//    /** The Constant INITIAL_VALUE. */
//    private static final int INITIAL_VALUE = 0;
//
//    /** The Constant FINAL_VALUE. */
//    private static final int FINAL_VALUE = 1;
//
//    /** The Constant FINAL_INDEX. */
//    private static final long FINAL_INDEX = 1;
//
//    /** The Constant SLIPNUMBER. */
//    private static final String SLIP_NUMBER = "scm.sales.orm.slipNumber.fieldLabel";
//
//    /** The Constant SCM_COM_SALES_CUSTOMER_MASTER. */
//    private static final String SCM_COM_SALES_CUSTOMER_MASTER = "SCM_COM_SALES_CUSTOMER_MASTER";
//
//    /** The Constant CUSTOMER_NAME. */
//    private static final String CUSTOMER_NAME = "customer_name";
//
//    /** The Constant CUSTOMER_CONTACT. */
//    private static final String CUSTOMER_CONTACT = "customer_contact_name";
//
//    /** The Constant SALES_REPRESENTATIVE. */
//    private static final String SALES_REPRESENTATIVE = "sales_representative";
//
//    /** The Constant DELIVERY_DESTINATION. */
//    private static final String DELIVERY_DESTINATION = "address_name";
//
//    /** The Constant APPLICATION_INDEX. */
//    private static final int APPLICATION_INDEX = 2;
//
//    /** The Constant POSTAL_CODE. */
//    private static final String POSTAL_CODE = "postal_code";
//
//    /** The Constant ADDRESS_LINE. */
//    private static final String ADDRESS_LINE = "address_line";
//
//    /** The Constant PHONE. */
//    private static final String PHONE = "phone";
//
//    /** The Constant FAX. */
//    private static final String FAX = "fax";
//
//    /** The Constant KEYWORD. */
//    private static final String KEYWORD = "keyword";
//
//    /** The Constant LONG_VALUE. */
//    private static final long LONG_VALUE = 0;
//
//    /** The Constant TOTAL_ORDER_AMOUNT. */
//    private static final String TOTAL_ORDER_AMOUNT = "orderEntryContents.salesOrderReceivedHeader.totalOrderAmount";
//
//    /** The Constant DISCOUNT_NAME_LABEL. */
//    private static final String DISCOUNT_NAME_LABEL = "SALE.SORM.discountNameLabel";
//
//    /** The Constant DISCOUNT_RATE_LABEL. */
//    private static final String DISCOUNT_RATE_LABEL = "SALE.SORM.discountRateLabel";
//
//    /** The Constant DISCOUNT_AMOUNT_LABEL. */
//    private static final String DISCOUNT_AMOUNT_LABEL = "SALE.SORM.discountAmountLabel";
//
//    /** The Constant DISCOUNT_LABEL. */
//    private static final String DISCOUNT_LABEL = "SALE.SORM.slipTotalAmount";
//
//    /** The Constant GET_PERCENTAGE. */
//    private static final int GET_PERCENTAGE = 100;
//
//    /** The DELIVERY_TIME_SEPERATOR. */
//    private static final int DELIVERY_TIME_SEPERATOR = 11;
//
//    /** The Constant NEGATIVE_VALUE. */
//    private static final int NEGATIVE_VALUE = -1;
//
//    /** The Constant DISCOUNT. */
//    private static final String DISCOUNT = "SALE.SORM.discount";
//
//    /** The Constant SUBTOTAL. */
//    private static final String SUBTOTAL = "SALE.SORM.subTotal";
//
//    @Override
//    public void insertSalesOrderEntity(SalesOrderEntity salesOrderEntity) {
//        salesOrderDao.insert(salesOrderEntity);
//    }
//
//    @Override
//    public void insertSalesOrderAppHstEntity(SalesOrderAppHstEntity salesOrderAppHstEntityList) {
//        salesOrderAppHstDao.insert(salesOrderAppHstEntityList);
//    }
//
//    @Override
//    public void insertSalesOrderAppHstEntityList(List<SalesOrderAppHstEntity> salesOrderAppHstEntityList) {
//        salesOrderAppHstDao.insertList(salesOrderAppHstEntityList);
//    }
//
//    @Override
//    public Map<String, Object> loadDatas(String applicationId) {
//        Map<String, Object> paramMap = new HashMap<>();
//        SalesOrderEntity salesOrderEntity = salesOrderDao.getSingle(applicationId);
//        if (Objects.nonNull(salesOrderEntity)) {
//            paramMap.put("orderApplicationNumber",
//                    salesOrderEntity.getSalesOrderApplicationNumber());
//            paramMap.put("salesOrderReceivedHeader", salesOrderEntity.getSalesOrderReceivedHeader());
//            paramMap.put("salesOrderReceivedDetail", salesOrderEntity
//                    .getSalesOrderReceivedDetailLocalList());
//            paramMap.put("salesCustomerName", salesOrderEntity.getSalesCustomerName());
//        }
//        return paramMap;
//    }
//
//    @Override
//    public SalesOrderEntity getEntity(String applicationId) {
//        Optional<SalesOrderEntity> salesOrderEntityOptional = Optional.empty();
//        SalesOrderEntity salesOrderEntity = new SalesOrderEntity();
//        if (Objects.nonNull(applicationId)) {
//            salesOrderEntityOptional = Optional.of(SalesOrderAppHstIndexEntity
//                    .formSalesOrderEntity(salesOrderAppHstDao
//                            .getSingleAppHstIndex(applicationId)));
//        }
//        if (salesOrderEntityOptional.isPresent()) {
//            salesOrderEntity = salesOrderEntityOptional.get();
//        }
//        return salesOrderEntity;
//    }
//
//    @Override
//    public SalesOrderAppHstEntity getAppHstEntity(String applicationId) {
//        SalesOrderAppHstEntity salesOrderAppHstEntity = new SalesOrderAppHstEntity();
//        Optional<SalesOrderAppHstEntity> salesOrderAppHstEntityOptional = Optional.empty();
//        if (Objects.nonNull(applicationId)) {
//            salesOrderAppHstEntityOptional = Optional.of(SalesOrderAppHstIndexEntity
//                    .formSalesOrderAppHstEntity(salesOrderAppHstDao
//                            .getSingleAppHstIndex(applicationId)));
//        }
//        if (salesOrderAppHstEntityOptional.isPresent()) {
//            salesOrderAppHstEntity = salesOrderAppHstEntityOptional.get();
//        }
//        return salesOrderAppHstEntity;
//    }
//
//    @Override
//    public Optional<SalesOrderEntity> getEntityByOrderReceivedId(UUID orderReceivedId) {
//        if (Objects.nonNull(orderReceivedId)) {
//            return Optional.ofNullable(this.salesOrderDao.getSingle(orderReceivedId));
//        } else {
//            return Optional.empty();
//        }
//    }
//
//    @Override
//    public Optional<SalesOrderAppHstEntity> getAppEntityByOrderReceivedId(UUID orderReceivedId) {
//        if (Objects.nonNull(orderReceivedId)) {
//            return Optional.ofNullable(this.salesOrderAppHstDao.getSingle(orderReceivedId));
//        } else {
//            return Optional.empty();
//        }
//    }
//
//    @Override
//    public Map<String, Object> fetchData(Map<String, Object> parameterMap) {
//        Map<String, Object> parameterDataMap = new HashMap<>();
//        SalesDeliveryDestinationMasterEntity salesDeliveryDestinationMasterEntity = salesDeliveryDestinationMasterService
//                .getDeliveryDestinationList(scmUserContext.getActiveCorpId())
//                .stream()
//                .filter(entity -> entity.getAddressId().equals(
//                        parameterMap.get("itemId").toString()))
//                .collect(Collectors.toList()).get(INITIAL_VALUE);
//        if (Objects.nonNull(salesDeliveryDestinationMasterEntity)) {
//            parameterDataMap.put("destinationName",
//                    salesDeliveryDestinationMasterEntity.getAddressName());
//            parameterDataMap.put("deliverypostalCode",
//                    salesDeliveryDestinationMasterEntity.getPostalCode());
//            parameterDataMap.put("deliveryAddress",
//                    salesDeliveryDestinationMasterEntity.getAddressLine());
//            parameterDataMap.put("deliveryPhone", salesDeliveryDestinationMasterEntity.getPhone());
//            parameterDataMap.put("deliveryFax", salesDeliveryDestinationMasterEntity.getFax());
//        }
//        return parameterDataMap;
//    }
//
//    @Override
//    public void setApplicationInformation(ApplicationId salesOrderApplicationId, String salesOrderApplicationNumber,
//            String salesOrderApplicationStatus,
//            String salesOrderSlipNumber) {
//        SalesOrderEntity salesOrderEntity = salesOrderDao.getSingle(salesOrderApplicationId);
//        if (Objects.nonNull(salesOrderEntity)) {
//            salesOrderEntity.setSalesOrderApplicationNumber(salesOrderApplicationNumber);
//            salesOrderEntity.setSalesOrderSlipNumber(salesOrderSlipNumber);
//            salesOrderEntity.setSalesOrderApplicationStatus(salesOrderApplicationStatus);
//            salesOrderDao.insert(salesOrderEntity);
//        }
//
//    }
//
//    @Override
//    public List<DeliveryDestinationVo> loadDataFromMaster(Map<String, Object> paramMap) {
//        List<DeliveryDestinationVo> deliveryDestinationVoList = new ArrayList<>();
//        List<SalesDeliveryDestinationMasterEntity> deliveryDestinationList =
//                salesDeliveryDestinationMasterService
//                        .getDeliveryDestinationList(scmUserContext.getActiveCorpId());
//        if (CollectionUtils.isNotEmpty(deliveryDestinationList)) {
//            return deliveryDestinationList.stream()
//                    .map(deliveryMasterObject -> getDetails(deliveryMasterObject))
//                    .collect(Collectors.toList());
//        }
//        return deliveryDestinationVoList;
//    }
//
//    @Override
//    public DeliveryDestinationVo getDetails(SalesDeliveryDestinationMasterEntity salesDeliveryDestinationMasterEntity) {
//        return DeliveryDestinationVo
//                .builder()
//                .customerName(salesDeliveryDestinationMasterEntity.getAddressId())
//                .destinationName(new WapTextLabelVo.Builder().label(
//                        salesDeliveryDestinationMasterEntity.getAddressName()).icon(CommonIconType.USER)
//                        .build())
//                .deliverypostalCode(new WapTextLabelVo.Builder().label(
//                        salesDeliveryDestinationMasterEntity.getPostalCode()).icon(CommonIconType.MOBILE)
//                        .build())
//                .deliveryAddress(new WapTextLabelVo.Builder().label(
//                        salesDeliveryDestinationMasterEntity.getAddressLine()).icon(CommonIconType.LOCATION)
//                        .build())
//                .deliveryPhone(new WapTextLabelVo.Builder().label(
//                        salesDeliveryDestinationMasterEntity.getPhone()).icon(CommonIconType.PHONE)
//                        .build())
//                .deliveryFax(new WapTextLabelVo.Builder().label(
//                        salesDeliveryDestinationMasterEntity.getFax()).icon(CommonIconType.PRINT)
//                        .build())
//                .build();
//    }
//
//    @Override
//    public List<SalesOrderReceivedDetailLocal>
//            removeEmptyDetails(List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList) {
//        List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailEmptyList = new ArrayList<>();
//        AtomicInteger rowIndex = new AtomicInteger();
//        TextMap textMap = textResourceManager.getTexts(Arrays.asList(TextId.of(DISCOUNT), TextId.of(SUBTOTAL)));
//        if (CollectionUtils.isNotEmpty(salesOrderReceivedDetailList)) {
//            salesOrderReceivedDetailList
//                    .removeIf(salesOrderReceivedDetail -> this.isEmptyRow(salesOrderReceivedDetail));
//            salesOrderReceivedDetailList.stream().forEach(
//                    detail -> {
//                        detail.setRowNumber(rowIndex.incrementAndGet());
//                        detail.setItemArrangementStatus(ItemArrangementStatusEnum.NOT_ARRANGED
//                                .getStatusValue());
//                        detail.setItemArrangementStatus(ItemArrangementStatusEnum.NOT_ARRANGED.getStatusValue());
//                        setDiscountValue(detail, salesOrderReceivedDetailList, textMap);
//                    });
//            return salesOrderReceivedDetailList;
//        } else {
//
//            return salesOrderReceivedDetailEmptyList;
//        }
//    }
//
//    /**
//     * setDiscountValue method sets the value for discount.
//     *
//     * @param salesOrderReceivedDetailLocal
//     *            the sales order received detail local is used to get the grid details
//     */
//    private void setDiscountValue(SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal,
//            List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList, TextMap textMap) {
//        int rowNumber = salesOrderReceivedDetailLocal.getRowNumber() - FINAL_VALUE;
//        int index = rowNumber - FINAL_VALUE;
//        List<TargetDetail> targetDetailList = new ArrayList<>();
//        if (salesOrderReceivedDetailLocal.getItemname().equals(textMap.get(DISCOUNT))) {
//            SalesOrderDiscount salesOrderDiscount = new SalesOrderDiscount();
//            salesOrderDiscount.setDiscountCalculationType(salesOrderReceivedDetailLocal.getDiscountDetailType());
//            salesOrderDiscount.setDiscountDocumentName(salesOrderReceivedDetailLocal.getDiscountName());
//            salesOrderDiscount.setDiscountDetailType(salesOrderReceivedDetailLocal.getDiscountType());
//            salesOrderDiscount.setDiscountRate(salesOrderReceivedDetailLocal.getDiscountRate());
//            if (salesOrderReceivedDetailLocal.getDiscountType()
//                    .equals(DiscountDetailType.ATTACHED_TO_DETAIL.toString())
//                    || salesOrderReceivedDetailLocal.getDiscountType()
//                            .equals(DiscountDetailType.SUBTOTAL.toString())) {
//                TargetDetail targetDetail = new TargetDetail();
//                targetDetail.setTargetNo(FINAL_VALUE);
//                targetDetail.setRowNo(rowNumber);
//                targetDetailList.add(targetDetail);
//            } else if (salesOrderReceivedDetailLocal.getDiscountType()
//                    .equals(DiscountDetailType.DETAIL.toString())) {
//                targetDetailList = setTargetDetailList(salesOrderReceivedDetailList, index, textMap.get(DISCOUNT));
//            }
//            salesOrderDiscount.setTargetDetail(targetDetailList);
//            salesOrderReceivedDetailLocal.setSalesOrderDiscount(salesOrderDiscount);
//        } else if (salesOrderReceivedDetailLocal.getItemname().equals(textMap.get(SUBTOTAL))) {
//            SalesOrderSubtotal salesOrderSubtotal = new SalesOrderSubtotal();
//            salesOrderSubtotal.setTargetDetail(setTargetDetailList(salesOrderReceivedDetailList, index,
//                    textMap.get(SUBTOTAL)));
//            salesOrderReceivedDetailLocal.setSalesOrderSubtotal(salesOrderSubtotal);
//        }
//
//    }
//
//    /**
//     * setTargetDetailList method sets the target value for discount.
//     *
//     * @param salesOrderReceivedDetailList
//     *            the sales order received detail local list is used to get the grid details
//     */
//    private List<TargetDetail> setTargetDetailList(List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList,
//            int index, String compareString) {
//        List<TargetDetail> targetDetailList = new ArrayList<>();
//        int targetNumber = FINAL_VALUE;
//        while (index >= INITIAL_VALUE) {
//            SalesOrderReceivedDetailLocal itemDetail = salesOrderReceivedDetailList.get(index);
//            if (!(itemDetail.getItemname().equals(compareString))) {
//                TargetDetail targetDetail = new TargetDetail();
//                targetDetail.setRowNo(itemDetail.getRowNumber());
//                targetDetail.setTargetNo(targetNumber);
//                targetDetailList.add(targetDetail);
//                targetNumber++;
//                index--;
//            } else {
//                index = NEGATIVE_VALUE;
//            }
//        }
//        return targetDetailList;
//    }
//
//    /**
//     * isEmptyRow method checks the empty row.
//     *
//     * @param salesOrderReceivedDetailLocal
//     *            the sales order received detail local is used to get the grid details
//     * @return boolean
//     */
//    private boolean isEmptyRow(SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal) {
//        boolean emptyRow = Objects.isNull(salesOrderReceivedDetailLocal.getQuantity())
//                || (salesOrderReceivedDetailLocal
//                        .getQuantity() <= INITIAL_VALUE);
//        emptyRow = emptyRow && StringUtils.isEmpty(salesOrderReceivedDetailLocal.getItemname());
//        emptyRow = emptyRow && StringUtils.isEmpty(salesOrderReceivedDetailLocal.getQuantityUnit());
//        emptyRow = emptyRow
//                && (Objects.isNull(salesOrderReceivedDetailLocal.getItemUnitPrice()) || (salesOrderReceivedDetailLocal
//                        .getItemUnitPrice().compareTo(BigDecimal.ZERO) <= INITIAL_VALUE));
//        return emptyRow && Objects.isNull(salesOrderReceivedDetailLocal.getItemDesiredDeliveryDate());
//    }
//
//    @Override
//    public void viewDetailPage(Map<String, Object> returnMap, String applicationId) {
//        SalesOrderAppHstEntity salesOrderAppHstEntity = getAppHstEntity(applicationId);
//        SalesOrderSlipDiscount salesOrderSlipDiscount = getRevisionNumber(applicationId);
//        returnMap.put("customerNameMasterInput", salesOrderAppHstEntity.getSalesCustomerName());
//        returnMap.put("salesRepresentativeTextInput", salesOrderAppHstEntity.getSalesRepresentative());
//        returnMap.put("customerContactMasterInput", salesOrderAppHstEntity.getSalesOrderReceivedHeader()
//                .getSalesCustomerContact());
//        returnMap.put("desiredDeliveryDateInput", salesOrderAppHstEntity.getSalesOrderReceivedHeader()
//                .getDesiredDeliveryDate());
//        returnMap.put("deliveryDestinationTextInput", salesOrderAppHstEntity.getSalesOrderReceivedHeader()
//                .getDeliveryDestination());
//        returnMap.put("postalCodeTextInput", salesOrderAppHstEntity.getSalesOrderReceivedHeader()
//                .getDeliveryPostalCode());
//        returnMap.put("addressTextInput", salesOrderAppHstEntity.getSalesOrderReceivedHeader().getDeliveryAddress());
//        returnMap.put("phoneTextInput", salesOrderAppHstEntity.getSalesOrderReceivedHeader().getDeliveryPhone());
//        returnMap.put("faxTextInput", salesOrderAppHstEntity.getSalesOrderReceivedHeader().getDeliveryFax());
//        List<SalesOrderFileUploader> salesOrderFileUploaderValidList = Objects.nonNull(salesOrderAppHstEntity
//                .getSalesOrderFileUploader())
//                ? salesOrderAppHstEntity.getSalesOrderFileUploader() : new ArrayList<>();
//        if (Objects.nonNull(salesOrderSlipDiscount)) {
//            if (Objects.nonNull(salesOrderSlipDiscount.getDiscountDocumentName())) {
//                BigDecimal totalAmount = salesOrderAppHstEntity.getSalesOrderReceivedHeader().getTotalOrderAmount()
//                        .subtract(
//                                salesOrderSlipDiscount.getTotalAmount());
//                returnMap.put("slipTotalAmountMoneyInput", totalAmount);
//                returnMap.put("slipDiscountMoneyInput", salesOrderSlipDiscount.getTotalAmount());
//                Map<String, Object> discountMap = loadDiscountGrid(salesOrderSlipDiscount);
//                discountMap.put("totalOrderAmount", totalAmount);
//                returnMap.put("discountOrderGrid", Arrays.asList(discountMap));
//            }
//        }
//        IvtlAnchorListItemsVo ivtlAnchorListItemsVo = new IvtlAnchorListItemsVo();
//        List<IvtlAnchorListItemVo> ivtlAnchorListItemVoList = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(salesOrderFileUploaderValidList)) {
//            salesOrderFileUploaderValidList.stream()
//                    .map(item -> ivtlAnchorListItemVoList.add(new IvtlAnchorListItemVo.Builder()
//                            .anchor(new WapAnchorLabelVo.Builder().label(item.getFileName()).build())
//                            .fileId(item.getFileId()).timeUUID(item.getTimeUUID())
//                            .userId(item.getFileUploaderUserId()).build())).collect(Collectors.toList());
//        }
//        ivtlAnchorListItemsVo.setChildren(ivtlAnchorListItemVoList);
//        returnMap.put("internalDocumentAnchorListItems", ivtlAnchorListItemsVo);
//        returnMap.put("customerOrderNoTextInput", salesOrderAppHstEntity.getSalesOrderReceivedHeader()
//                .getCustomerSalesOrderNumber());
//        returnMap.put("salesOrderTotalAmountLabel", salesOrderAppHstEntity.getSalesOrderReceivedHeader()
//                .getTotalOrderAmount());
//        returnMap.put("totalOrderAmount", salesOrderAppHstEntity.getSalesOrderReceivedHeader().getTotalOrderAmount());
//        if (Objects.nonNull(salesOrderAppHstEntity.getSalesOrderReceivedDetail())) {
//            List<Map<String, Object>> gridList = salesOrderAppHstEntity.getSalesOrderReceivedDetail()
//                    .stream()
//                    .map(orderDetailsLineRow -> loadGridData(orderDetailsLineRow)).collect(Collectors.toList());
//            returnMap.put("salesOrderGrid", gridList);
//        }
//    }
//
//    /**
//     * loadGridData method is used to load the grid data values.
//     *
//     * @param orderDetailsLineRow
//     *            it contains the grid details.
//     * @return orderDetailsLineMap contains the column name and value.
//     */
//    private Map<String, Object> loadGridData(SalesOrderReceivedDetailLocal orderDetailsLineRow) {
//        Map<String, Object> orderDetailsLineMap = new HashMap<>();
//        orderDetailsLineMap.put("itemNameGridColumn",
//                orderDetailsLineRow.getItemname());
//        orderDetailsLineMap.put("quantityGridColumn",
//                orderDetailsLineRow.getQuantity());
//        orderDetailsLineMap.put("quantityUnitGridColumn",
//                orderDetailsLineRow.getQuantityUnit());
//        orderDetailsLineMap.put("itemUnitPriceGridColumn",
//                orderDetailsLineRow.getItemUnitPrice());
//        orderDetailsLineMap.put("totalAmountGridColumn",
//                orderDetailsLineRow.getTotalAmount());
//        orderDetailsLineMap.put("itemDesiredDeliveryDateGridColumn",
//                orderDetailsLineRow.getItemDesiredDeliveryDate());
//        orderDetailsLineMap.put("remarksGridColumn",
//                orderDetailsLineRow.getRemarks());
//        return orderDetailsLineMap;
//
//    }
//
//    /**
//     * loadDiscountGrid,Loads the discount grid.
//     *
//     * @param salesOrderSlipDiscount
//     *            the sales order slip discount
//     * @return the object
//     */
//    private Map<String, Object> loadDiscountGrid(SalesOrderSlipDiscount salesOrderSlipDiscount) {
//        Map<String, Object> discountMap = new HashMap<>();
//        TextMap textMap = textResourceManager.getTexts(Arrays.asList(TextId.of(DISCOUNT_NAME_LABEL),
//                TextId.of(DISCOUNT_RATE_LABEL), TextId.of(DISCOUNT_AMOUNT_LABEL),
//                TextId.of(DISCOUNT_LABEL)));
//        discountMap.put("discountNameLabel", textMap.get(DISCOUNT_NAME_LABEL));
//        discountMap.put("discountRateLabel", textMap.get(DISCOUNT_RATE_LABEL));
//        discountMap.put("discountAmountLabel", textMap.get(DISCOUNT_AMOUNT_LABEL));
//        discountMap.put("discountLabel", textMap.get(DISCOUNT_LABEL));
//        discountMap.put("discountRate", salesOrderSlipDiscount.getDiscountRate());
//        discountMap.put("discountAmount", salesOrderSlipDiscount.getTotalAmount());
//        discountMap.put("discountName", salesOrderSlipDiscount.getDiscountDocumentName());
//        return discountMap;
//    }
//
//    @Override
//    public void setApprovalData(FlowEventListenerParameterVo flowEventListenerParameterVo) {
//        String statusValue = flowEventListenerParameterVo.getOperationType().getValue();
//        String applicationId = flowEventListenerParameterVo.getApplication().getApplicationId().toString();
//        SalesOrderEntity salesOrderEntity = getEntity(applicationId);
//        SalesOrderSlipDiscount salesOrderSlipDiscount = getRevisionNumber(applicationId);
//        SalesOrderAppHstEntity salesOrderAppHstEntity = getAppHstEntity(applicationId);
//        salesOrderAppHstEntity.setSalesOrderSlipDiscount(salesOrderSlipDiscount);
//        SalesOrderAppHstEntity salesOrderAppHstTemporaryEntity = getAppHstEntity(applicationId);
//        salesOrderAppHstTemporaryEntity.setSalesOrderSlipDiscount(salesOrderSlipDiscount);
//        salesOrderEntity.setSalesOrderSlipDiscount(salesOrderSlipDiscount);
//        AtomicInteger rowIndex = new AtomicInteger();
//        if (statusValue.equalsIgnoreCase(FlowOperationType.APPROVE.toString())) {
//            SalesOrderApplicationApprovalStatusEnum salesOrderApplicationApprovalStatusEnum = SalesOrderApplicationApprovalStatusEnum
//                    .valueOf(salesOrderAppHstEntity.getSalesOrderApplicationApprovalStatus());
//            switch (salesOrderApplicationApprovalStatusEnum) {
//            case CANCEL_SUBMITTED:
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.CANCELED
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.NEW
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity.setRevisionNumber(salesOrderAppHstEntity.getRevisionNumber()
//                        + FINAL_INDEX);
//                salesOrderAppHstTemporaryEntity.setLatestRevisionFlg(FINAL_VALUE);
//                salesOrderAppHstEntity.setLatestRevisionFlg(INITIAL_VALUE);
//                salesOrderEntity
//                        .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.CANCELED
//                                .getStatusValue());
//                salesOrderEntity
//                        .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.NEW
//                                .getStatusValue());
//                break;
//            case UPDATE_SUBMITTED:
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.APPROVED
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.UPDATED
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity.setRevisionNumber(salesOrderAppHstEntity.getRevisionNumber()
//                        + FINAL_INDEX);
//                salesOrderAppHstTemporaryEntity.setLatestRevisionFlg(FINAL_VALUE);
//                salesOrderAppHstEntity.setLatestRevisionFlg(INITIAL_VALUE);
//                salesOrderEntity
//                        .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.APPROVED
//                                .getStatusValue());
//                salesOrderEntity
//                        .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.UPDATED
//                                .getStatusValue());
//                break;
//            case SUBMITTED:
//                salesOrderEntity
//                        .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.APPROVED
//                                .getStatusValue());
//                salesOrderEntity
//                        .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.NEW
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.APPROVED
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.NEW
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity.setRevisionNumber(salesOrderAppHstEntity.getRevisionNumber()
//                        + FINAL_INDEX);
//                salesOrderAppHstTemporaryEntity.setLatestRevisionFlg(FINAL_VALUE);
//                salesOrderAppHstEntity.setLatestRevisionFlg(INITIAL_VALUE);
//                break;
//            default:
//                break;
//            }
//            salesOrderDao.insert(salesOrderEntity);
//            salesOrderAppHstDao.insert(salesOrderAppHstEntity);
//            salesOrderAppHstDao.insert(salesOrderAppHstTemporaryEntity);
//        } else if (statusValue.equalsIgnoreCase(FlowOperationType.REJECT.toString())) {
//
//            List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList = salesOrderAppHstEntity
//                    .getSalesOrderReceivedDetail();
//            salesOrderReceivedDetailList = removeEmptyDetails(salesOrderReceivedDetailList);
//            SalesOrderApplicationApprovalStatusEnum salesOrderApplicationApprovalStatusEnum = SalesOrderApplicationApprovalStatusEnum
//                    .valueOf(salesOrderAppHstEntity.getSalesOrderApplicationApprovalStatus());
//            switch (salesOrderApplicationApprovalStatusEnum) {
//            /**
//             * In 17.09, Order received entry does not support Approval feature. code as follows is written under
//             * temporary specification. If cancel submission is rejected, order slip's approval status back to approved.
//             * Because DTO to be contained original data is not defined in 17.09.
//             *
//             * It should be another status like CANCEL_REJECTED, and values of order slip should be recovered with
//             * original values.
//             */
//            case CANCEL_SUBMITTED:
//                salesOrderReceivedDetailList
//                        .stream()
//                        .forEach(
//                                detail -> {
//                                    detail.setRowNumber(rowIndex.incrementAndGet());
//                                    detail.setItemArrangementStatus(ItemArrangementStatusEnum.NOT_ARRANGED
//                                            .getStatusValue());
//                                });
//                salesOrderAppHstTemporaryEntity.setSalesOrderReceivedDetail(salesOrderReceivedDetailList);
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.REJECTED
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.NEW
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity.setRevisionNumber(salesOrderAppHstEntity.getRevisionNumber()
//                        + FINAL_INDEX);
//                salesOrderAppHstTemporaryEntity.setLatestRevisionFlg(FINAL_VALUE);
//                salesOrderAppHstEntity.setLatestRevisionFlg(INITIAL_VALUE);
//                break;
//            case UPDATE_SUBMITTED:
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.REJECTED
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.UPDATED
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity.setRevisionNumber(salesOrderAppHstEntity.getRevisionNumber()
//                        + FINAL_INDEX);
//                salesOrderAppHstTemporaryEntity.setLatestRevisionFlg(FINAL_VALUE);
//                salesOrderAppHstEntity.setLatestRevisionFlg(INITIAL_VALUE);
//                break;
//            default:
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.REJECTED
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity
//                        .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.NEW
//                                .getStatusValue());
//                salesOrderAppHstTemporaryEntity.setRevisionNumber(salesOrderAppHstEntity.getRevisionNumber()
//                        + FINAL_INDEX);
//                salesOrderAppHstTemporaryEntity.setLatestRevisionFlg(FINAL_VALUE);
//                salesOrderAppHstEntity.setLatestRevisionFlg(INITIAL_VALUE);
//                break;
//            }
//            salesOrderAppHstDao.insert(salesOrderAppHstEntity);
//            salesOrderAppHstDao.insert(salesOrderAppHstTemporaryEntity);
//
//        } else {
//            if (statusValue.equalsIgnoreCase(FlowOperationType.REMAND.toString())) {
//
//                List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList = salesOrderAppHstEntity
//                        .getSalesOrderReceivedDetail();
//                salesOrderReceivedDetailList = removeEmptyDetails(salesOrderReceivedDetailList);
//                SalesOrderApplicationApprovalStatusEnum salesOrderApplicationApprovalStatusEnum = SalesOrderApplicationApprovalStatusEnum
//                        .valueOf(salesOrderAppHstEntity.getSalesOrderApplicationApprovalStatus());
//
//                switch (salesOrderApplicationApprovalStatusEnum) {
//                /**
//                 * In 17.09, Order received entry does not support Approval feature. code as follows is written under
//                 * temporary specification. If cancel submission is remanded, order slip's approval status back to
//                 * approved. Because DTO to be contained original data is not defined in 17.09.
//                 *
//                 * It should be another status like CANCEL_REMANDED, and values of order slip should be recovered with
//                 * original values.
//                 */
//
//                case CANCEL_SUBMITTED:
//                    salesOrderReceivedDetailList
//                            .stream()
//                            .forEach(
//                                    detail -> {
//                                        detail.setRowNumber(rowIndex.incrementAndGet());
//                                        detail.setItemArrangementStatus(ItemArrangementStatusEnum.NOT_ARRANGED
//                                                .getStatusValue());
//                                    });
//                    salesOrderAppHstTemporaryEntity.setSalesOrderReceivedDetail(salesOrderReceivedDetailList);
//                    salesOrderAppHstTemporaryEntity
//                            .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.REMANDED
//                                    .getStatusValue());
//                    salesOrderAppHstTemporaryEntity
//                            .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.NEW
//                                    .getStatusValue());
//                    salesOrderAppHstTemporaryEntity.setRevisionNumber(salesOrderAppHstEntity.getRevisionNumber()
//                            + FINAL_INDEX);
//                    salesOrderAppHstTemporaryEntity.setLatestRevisionFlg(FINAL_VALUE);
//                    salesOrderAppHstEntity.setLatestRevisionFlg(INITIAL_VALUE);
//                    break;
//                case UPDATE_SUBMITTED:
//                    salesOrderAppHstTemporaryEntity
//                            .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.REMANDED
//                                    .getStatusValue());
//                    salesOrderAppHstTemporaryEntity
//                            .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.UPDATED
//                                    .getStatusValue());
//                    salesOrderAppHstTemporaryEntity.setRevisionNumber(salesOrderAppHstEntity.getRevisionNumber()
//                            + FINAL_INDEX);
//                    salesOrderAppHstTemporaryEntity.setLatestRevisionFlg(FINAL_VALUE);
//                    salesOrderAppHstEntity.setLatestRevisionFlg(INITIAL_VALUE);
//                    break;
//                default:
//                    salesOrderAppHstTemporaryEntity
//                            .setSalesOrderApplicationApprovalStatus(SalesOrderApplicationApprovalStatusEnum.REMANDED
//                                    .getStatusValue());
//                    salesOrderAppHstTemporaryEntity
//                            .setSalesOrderApplicationStatus(SalesOrderApplicationStatusEnum.NEW
//                                    .getStatusValue());
//                    salesOrderAppHstTemporaryEntity.setRevisionNumber(salesOrderAppHstEntity.getRevisionNumber()
//                            + FINAL_INDEX);
//                    salesOrderAppHstTemporaryEntity.setLatestRevisionFlg(FINAL_VALUE);
//                    salesOrderAppHstEntity.setLatestRevisionFlg(INITIAL_VALUE);
//                    break;
//                }
//                salesOrderAppHstDao.insert(salesOrderAppHstEntity);
//                salesOrderAppHstDao.insert(salesOrderAppHstTemporaryEntity);
//            }
//        }
//    }
//
//    @Override
//    public OrderEntryContents setDefaultValues(OrderEntryContents orderEntryContents) {
//        List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList = orderEntryContents
//                .getSalesOrderReceivedDetailList();
//        if (CollectionUtils.isNotEmpty(salesOrderReceivedDetailList)) {
//            salesOrderReceivedDetailList
//                    .stream()
//                    .map(salesOrderReceivedDetailLocal ->
//                            setGridValues(salesOrderReceivedDetailLocal)
//                    ).collect(Collectors.toList());
//            orderEntryContents.setSalesOrderReceivedDetailList(salesOrderReceivedDetailList);
//        }
//        return orderEntryContents;
//
//    }
//
//    /**
//     * setGridValues is used to set the values in the grid.
//     *
//     * @param salesOrderReceivedDetailLocal
//     *            the sales order received detail local is used to get the grid headers
//     *
//     * @return salesOrderReceivedDetailLocal to set grid headers
//     */
//    private SalesOrderReceivedDetailLocal setGridValues(SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal) {
//        if (Objects.nonNull(salesOrderReceivedDetailLocal.getItemname())
//                && !salesOrderReceivedDetailLocal.getItemname().equalsIgnoreCase(StringUtils.EMPTY)) {
//            setItemValues(salesOrderReceivedDetailLocal);
//        }
//        return salesOrderReceivedDetailLocal;
//    }
//
//    /**
//     * setItemValues is used to set the item grid values according to input values
//     *
//     * @param salesOrderReceivedDetailLocal
//     *            the sales order received detail local is used to get the grid headers
//     */
//    private void setItemValues(SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal) {
//        if (Objects.isNull(salesOrderReceivedDetailLocal.getQuantity())
//                || (salesOrderReceivedDetailLocal.getQuantity() < INITIAL_VALUE)) {
//            salesOrderReceivedDetailLocal.setQuantity(INITIAL_VALUE);
//        }
//        if (Objects.isNull(salesOrderReceivedDetailLocal.getQuantityUnit())
//                || (salesOrderReceivedDetailLocal.getQuantityUnit().equalsIgnoreCase(StringUtils.EMPTY))) {
//            salesOrderReceivedDetailLocal.setQuantityUnit(textResourceManager.getText(
//                    (TextId.of("SALE.SORM.quantity.check"))));
//        }
//        if (Objects.isNull(salesOrderReceivedDetailLocal.getItemUnitPrice())
//                || (salesOrderReceivedDetailLocal.getItemUnitPrice().compareTo(BigDecimal.ZERO) < INITIAL_VALUE)) {
//            salesOrderReceivedDetailLocal.setItemUnitPrice(BigDecimal.ZERO);
//        }
//        if (Objects.isNull(salesOrderReceivedDetailLocal.getTotalAmount())
//                || (salesOrderReceivedDetailLocal.getTotalAmount().compareTo(BigDecimal.ZERO) < INITIAL_VALUE)) {
//            salesOrderReceivedDetailLocal.setTotalAmount(BigDecimal.ZERO);
//        }
//        if (Objects.isNull(salesOrderReceivedDetailLocal.getItemDesiredDeliveryDate())) {
//            salesOrderReceivedDetailLocal.setItemDesiredDeliveryDate(LocalDate.now());
//        }
//    }
//
//    @Override
//    public ResponseItemMap getCalculatedAmount(ApplicationEntity<OrderEntryContents> applicationEntity,
//            List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList) {
//        ResponseItemMap responseMap = new ResponseItemMap();
//        SalesOrderReceivedHeader salesOrderReceivedHeader = applicationEntity.getBusinessData()
//                .getSalesOrderReceivedHeader();
//        List<SalesOrderDiscountSlip> salesOrderDiscountSlipList = applicationEntity.getBusinessData()
//                .getSalesOrderDiscountSlipList();
//        TextMap textMap = textResourceManager.getTexts(Arrays.asList(TextId.of(DISCOUNT_NAME_LABEL),
//                TextId.of(DISCOUNT_RATE_LABEL), TextId.of(DISCOUNT_AMOUNT_LABEL),
//                TextId.of(DISCOUNT_LABEL), TextId.of(DISCOUNT), TextId.of(SUBTOTAL)));
//        salesOrderReceivedDetailList.stream().map(
//                detail -> {
//                    try {
//                        if (!(detail.getItemname().equals(textMap.get(SUBTOTAL)) || detail
//                                .getItemname().equals(textMap.get(DISCOUNT)))) {
//                            setCalculatedAmount(detail, salesOrderReceivedHeader, applicationEntity);
//                        } else if (detail.getItemname().equals(textMap.get(SUBTOTAL))) {
//                            setSubTotalAmount(detail, salesOrderReceivedDetailList);
//                        } else if (detail.getItemname().equals(textMap.get(DISCOUNT))) {
//                            setDiscountAmount(detail, salesOrderReceivedDetailList);
//                        }
//                    } catch (Exception calclulationAmountException) {
//                        calclulationAmountException.printStackTrace();
//                    }
//                    return detail;
//                }).collect(Collectors.toList());
//        List<BigDecimal> netAmountList = salesOrderReceivedDetailList.stream()
//                .filter(predicate -> !(predicate.getItemname().equals(textMap.get(SUBTOTAL))))
//                .map(SalesOrderReceivedDetailLocal::getTotalAmount)
//                .collect(Collectors.toList());
//        BigDecimal totalObtainedAmount = netAmountList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
//        if (CollectionUtils.isNotEmpty(salesOrderDiscountSlipList)) {
//            if (Objects.nonNull(salesOrderDiscountSlipList.get(INITIAL_VALUE).getDiscountName())
//                    && StringUtils.isNotEmpty(salesOrderDiscountSlipList.get(INITIAL_VALUE).getDiscountName())) {
//                BigDecimal totalDiscountAmount = calculateTotalDiscountAmount(salesOrderDiscountSlipList,
//                        totalObtainedAmount);
//                BigDecimal totalDiscountedAmount = totalObtainedAmount.add(totalDiscountAmount);
//                salesOrderDiscountSlipList.get(INITIAL_VALUE).setTotalOrderAmount(totalObtainedAmount);
//                salesOrderDiscountSlipList.get(INITIAL_VALUE).setDiscountAmount(totalDiscountAmount);
//                salesOrderDiscountSlipList.get(INITIAL_VALUE).setDescriptionName(StringUtils.EMPTY);
//                responseMap.setValue("orderEntryContents.slipDiscount",
//                        totalDiscountAmount);
//                responseMap.setValue(TOTAL_ORDER_AMOUNT,
//                        totalDiscountedAmount);
//            }
//            else {
//                salesOrderDiscountSlipList.get(INITIAL_VALUE).setTotalOrderAmount(BigDecimal.ZERO);
//                salesOrderDiscountSlipList.get(INITIAL_VALUE).setDiscountAmount(BigDecimal.ZERO);
//                salesOrderDiscountSlipList.get(INITIAL_VALUE).setDiscountRate(INITIAL_VALUE);
//                salesOrderDiscountSlipList.get(INITIAL_VALUE).setDescriptionName(StringUtils.EMPTY);
//                responseMap.setValue("orderEntryContents.slipDiscount", BigDecimal.ZERO);
//                responseMap.setValue(TOTAL_ORDER_AMOUNT,
//                        totalObtainedAmount);
//            }
//            responseMap.setValue("orderEntryContents.slipTotalAmount",
//                    totalObtainedAmount);
//            salesOrderDiscountSlipList.get(INITIAL_VALUE).setTotalOrderAmount(totalObtainedAmount);
//            salesOrderDiscountSlipList.get(INITIAL_VALUE).setDiscountNameLabel(
//                    textMap.get(DISCOUNT_NAME_LABEL));
//            salesOrderDiscountSlipList.get(INITIAL_VALUE).setDiscountRateLabel(
//                    textMap.get(DISCOUNT_RATE_LABEL));
//            salesOrderDiscountSlipList.get(INITIAL_VALUE).setDiscountAmountLabel(
//                    textMap.get(DISCOUNT_AMOUNT_LABEL));
//            salesOrderDiscountSlipList.get(INITIAL_VALUE).setDiscountLabel(textMap.get(DISCOUNT_LABEL));
//        } else {
//            responseMap.setValue(TOTAL_ORDER_AMOUNT, totalObtainedAmount);
//        }
//        responseMap.setValue("orderEntryContents.salesOrderReceivedDetailList", salesOrderReceivedDetailList);
//        responseMap.setValue("orderEntryContents.salesOrderDiscountSlipList", salesOrderDiscountSlipList);
//        return responseMap;
//    }
//
//    /**
//     * setSubTotalAmount,Calculates the sub total amount.
//     *
//     * @param salesOrderReceivedDetailLocal
//     *            the sales order detail item
//     * @param salesOrderReceivedDetailList
//     *            the sales order received detail list
//     */
//    private void setSubTotalAmount(SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal,
//            List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList) {
//        TextMap textMap = textResourceManager.getTexts(Arrays.asList(TextId.of(DISCOUNT), TextId.of(SUBTOTAL)));
//        int rowNumber = salesOrderReceivedDetailLocal.getRowNumber() - FINAL_VALUE;
//        int index = rowNumber - FINAL_VALUE;
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        while (index >= INITIAL_VALUE) {
//            SalesOrderReceivedDetailLocal itemDetail = salesOrderReceivedDetailList.get(index);
//            if (!(itemDetail.getItemname().equals(textMap.get(SUBTOTAL)))) {
//                totalAmount = totalAmount.add(itemDetail.getTotalAmount());
//                index--;
//            } else {
//                index = NEGATIVE_VALUE;
//            }
//        }
//        salesOrderReceivedDetailLocal.setTotalAmount(totalAmount);
//    }
//
//    /**
//     * setDiscountAmount,Calculates the discount amount.
//     *
//     * @param salesOrderReceivedDetailLocal
//     *            the sales order detail item
//     * @param salesOrderReceivedDetailList
//     *            the sales order received detail list
//     */
//    private void setDiscountAmount(SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal,
//            List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList) {
//        TextMap textMap = textResourceManager.getTexts(Arrays.asList(TextId.of(DISCOUNT), TextId.of(SUBTOTAL)));
//        int rowNumber = salesOrderReceivedDetailLocal.getRowNumber() - FINAL_VALUE;
//        int index = rowNumber - FINAL_VALUE;
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        if (salesOrderReceivedDetailLocal.getDiscountType()
//                .equals(DiscountDetailType.ATTACHED_TO_DETAIL.toString())
//                || salesOrderReceivedDetailLocal.getDiscountType()
//                        .equals(DiscountDetailType.SUBTOTAL.toString())) {
//            totalAmount = salesOrderReceivedDetailList.get(rowNumber - FINAL_VALUE).getTotalAmount();
//            updateDiscountValue(salesOrderReceivedDetailLocal, totalAmount);
//            if (salesOrderReceivedDetailLocal.getDiscountDetailType().equals(
//                    DiscountCalculationType.DISCOUNT_RATE.toString())) {
//                salesOrderReceivedDetailLocal.setDiscountAmount((new BigDecimal(salesOrderReceivedDetailLocal
//                        .getDiscountRate()).divide(new BigDecimal(100))).multiply(totalAmount));
//            } else if (salesOrderReceivedDetailLocal.getDiscountDetailType().equals(
//                    DiscountCalculationType.DETAIL_PRICE.toString())) {
//                salesOrderReceivedDetailLocal.setDiscountRate(INITIAL_VALUE);
//                salesOrderReceivedDetailLocal.setDiscountAmount(salesOrderReceivedDetailLocal.getDiscountAmount());
//            }
//        } else if (salesOrderReceivedDetailLocal.getDiscountType().equals(DiscountDetailType.DETAIL.toString())) {
//            while (index >= INITIAL_VALUE) {
//                SalesOrderReceivedDetailLocal itemDetail = salesOrderReceivedDetailList.get(index);
//                if (!((itemDetail.getItemname().equals(textMap.get(DISCOUNT))) || (itemDetail.getItemname()
//                        .equals(textMap.get(SUBTOTAL))))) {
//                    totalAmount = totalAmount.add(itemDetail.getTotalAmount());
//                    index--;
//                } else {
//                    index = NEGATIVE_VALUE;
//                }
//            }
//            updateDiscountValue(salesOrderReceivedDetailLocal, totalAmount);
//            if (salesOrderReceivedDetailLocal.getDiscountDetailType().equals(
//                    DiscountCalculationType.DISCOUNT_RATE.toString())) {
//                salesOrderReceivedDetailLocal.setDiscountAmount((new BigDecimal(salesOrderReceivedDetailLocal
//                        .getDiscountRate()).divide(new BigDecimal(100))).multiply(totalAmount));
//            } else if (salesOrderReceivedDetailLocal.getDiscountDetailType().equals(
//                    DiscountCalculationType.DETAIL_PRICE.toString())) {
//                salesOrderReceivedDetailLocal.setDiscountRate(INITIAL_VALUE);
//                salesOrderReceivedDetailLocal.setDiscountAmount(salesOrderReceivedDetailLocal.getDiscountAmount());
//            }
//        }
//        if (salesOrderReceivedDetailLocal.getDiscountAmount().intValue() > INITIAL_VALUE) {
//            salesOrderReceivedDetailLocal.setDiscountAmount(salesOrderReceivedDetailLocal.getDiscountAmount()
//                    .negate());
//        }
//        salesOrderReceivedDetailLocal.setTotalAmount(salesOrderReceivedDetailLocal.getDiscountAmount());
//    }
//
//    /**
//     * updateDiscountValue,updates the discount value based on discount type.
//     *
//     * @param salesOrderReceivedDetailLocal
//     *            the sales order detail item
//     * @param totalAmount
//     *            the total amount of the order slip
//     */
//    private void
//            updateDiscountValue(SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal, BigDecimal totalAmount) {
//        if (salesOrderReceivedDetailLocal.getDiscountDetailType().equals(
//                DiscountCalculationType.DISCOUNT_RATE.toString())) {
//            salesOrderReceivedDetailLocal.setDiscountAmount((new BigDecimal(salesOrderReceivedDetailLocal
//                    .getDiscountRate()).divide(new BigDecimal(100))).multiply(totalAmount));
//        } else if (salesOrderReceivedDetailLocal.getDiscountDetailType().equals(
//                DiscountCalculationType.DETAIL_PRICE.toString())) {
//            salesOrderReceivedDetailLocal.setDiscountRate(INITIAL_VALUE);
//            salesOrderReceivedDetailLocal.setDiscountAmount(salesOrderReceivedDetailLocal.getDiscountAmount());
//        } else if (salesOrderReceivedDetailLocal.getDiscountDetailType().equals(
//                DiscountCalculationType.UNIT_PRICE.toString())) {
//            salesOrderReceivedDetailLocal.setDiscountAmount((new BigDecimal(salesOrderReceivedDetailLocal
//                    .getQuantity()).multiply(salesOrderReceivedDetailLocal.getItemPrice())));
//        }
//    }
//
//    /**
//     * calculateTotalDiscountAmount,Calculates the total discount amount.
//     *
//     * @param salesOrderDiscountSlipList
//     *            the sales order discount slip list
//     * @param totalObtainedAmount
//     *            the total obtained order amount
//     * @return the total discount amount
//     */
//    private BigDecimal calculateTotalDiscountAmount(List<SalesOrderDiscountSlip> salesOrderDiscountSlipList,
//            BigDecimal totalObtainedAmount) {
//        BigDecimal totalDiscountAmount = BigDecimal.ZERO;
//        if (salesOrderDiscountSlipList.get(INITIAL_VALUE).getDiscountDetailType()
//                .equals(DiscountCalculationType.DISCOUNT_RATE.toString())) {
//            totalDiscountAmount = totalObtainedAmount.multiply(new BigDecimal(salesOrderDiscountSlipList
//                    .get(INITIAL_VALUE).getDiscountRate()).divide(new BigDecimal(GET_PERCENTAGE))).negate();
//        } else {
//            if (salesOrderDiscountSlipList.get(INITIAL_VALUE).getDiscountDetailType()
//                    .equals(DiscountCalculationType.DETAIL_PRICE.toString())) {
//                totalDiscountAmount = salesOrderDiscountSlipList.get(INITIAL_VALUE).getDiscountAmount();
//                if (Objects.nonNull(totalDiscountAmount)) {
//                    if (Objects.equals(totalDiscountAmount.signum(), FINAL_VALUE)) {
//                        totalDiscountAmount = totalDiscountAmount.negate();
//                    }
//                }
//            }
//        }
//        return totalDiscountAmount;
//    }
//
//    /**
//     * setCalculatedAmount is used to set the calculated amount in the grid.
//     *
//     * @param salesOrderReceivedDetailLocal
//     *            the sales order received detail local is used to get the grid details
//     * @param salesOrderReceivedHeader
//     *            the sales order received header is used to get the grid headers
//     * @param applicationEntity
//     * @throws TaxCalcException
//     */
//    private void setCalculatedAmount(SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal,
//            SalesOrderReceivedHeader salesOrderReceivedHeader, ApplicationEntity<OrderEntryContents> applicationEntity)
//            throws TaxCalcException {
//        MathContext roundSetting = new MathContext(INITIAL_VALUE, RoundingMode.DOWN);
//        if (StringUtils.isNotEmpty(salesOrderReceivedDetailLocal.getItemname())) {
//            if (Objects.nonNull(salesOrderReceivedDetailLocal.getItemUnitPrice())
//                    && Objects.nonNull(salesOrderReceivedDetailLocal.getQuantity())) {
//                BigDecimal itemUnitPrice =
//                        salesOrderReceivedDetailLocal.getItemUnitPrice().setScale(INITIAL_VALUE, RoundingMode.HALF_UP);
//                BigDecimal totalAmount = itemUnitPrice.multiply(
//                        BigDecimal.valueOf((salesOrderReceivedDetailLocal.getQuantity())),
//                        roundSetting);
//                salesOrderReceivedDetailLocal.setTotalAmount(totalAmount);
//            } else {
//                salesOrderReceivedDetailLocal.setTotalAmount(BigDecimal.ZERO);
//            }
//            if ((Objects.isNull(salesOrderReceivedDetailLocal.getQuantity()))
//                    || (salesOrderReceivedDetailLocal.getQuantity() <= INITIAL_VALUE)) {
//                salesOrderReceivedDetailLocal.setQuantity(INITIAL_VALUE);
//            }
//            if ((Objects.isNull(salesOrderReceivedDetailLocal.getItemUnitPrice()))
//                    || (salesOrderReceivedDetailLocal.getItemUnitPrice().compareTo(BigDecimal.ZERO) <= INITIAL_VALUE)) {
//                salesOrderReceivedDetailLocal.setItemUnitPrice(BigDecimal.ZERO);
//            }
//            if (Objects.nonNull(salesOrderReceivedHeader.getDesiredDeliveryDate())
//                    && Objects.isNull(salesOrderReceivedDetailLocal.getItemDesiredDeliveryDate())) {
//                salesOrderReceivedDetailLocal.setItemDesiredDeliveryDate(salesOrderReceivedHeader
//                        .getDesiredDeliveryDate());
//            }
//            if (StringUtils.isNotEmpty(salesOrderReceivedDetailLocal.getTaxDivisionCode())) {
//                taxDivisioncaluculation(applicationEntity, salesOrderReceivedDetailLocal);
//            }
//            if (StringUtils.isNotEmpty(applicationEntity.getBusinessData().getRateTypeCode())
//                    && StringUtils.isNotEmpty(salesOrderReceivedDetailLocal.getTaxDivisionCode())) {
//                convertionTaxCalculation(applicationEntity, salesOrderReceivedDetailLocal);
//
//            }
//        } else {
//            salesOrderReceivedDetailLocal.setTotalAmount(null);
//            salesOrderReceivedDetailLocal.setQuantityUnit(null);
//            salesOrderReceivedDetailLocal.setRemarks(null);
//            salesOrderReceivedDetailLocal.setItemUnitPrice(null);
//            salesOrderReceivedDetailLocal.setItemDesiredDeliveryDate(null);
//        }
//    }
//
//    @Override
//    public Map<String, Object> checkCustomerName(String searchKey) {
//        List<HitDocument> hitDocumentList = getDataFromElastic(searchKey, Arrays.asList(
//                CUSTOMER_NAME, SALES_REPRESENTATIVE,
//                CUSTOMER_CONTACT, DELIVERY_DESTINATION,
//                POSTAL_CODE, ADDRESS_LINE, PHONE,
//                FAX));
//        Map<String, Object> paramDataMap = new HashMap<>();
//        if (CollectionUtils.isNotEmpty(hitDocumentList)) {
//            paramDataMap.put(KEYWORD, CollectionUtils.isNotEmpty(hitDocumentList));
//            Map<String, String> hitMap = new HashMap<>();
//            HitDocument hitDocument = hitDocumentList.get(INITIAL_VALUE);
//            hitMap.put(CUSTOMER_NAME,
//                    hitDocument.getDocField().get(CUSTOMER_NAME).toString());
//            hitMap.put(SALES_REPRESENTATIVE,
//                    hitDocument.getDocField().get(SALES_REPRESENTATIVE).toString());
//            hitMap.put(CUSTOMER_CONTACT,
//                    hitDocument.getDocField().get(CUSTOMER_CONTACT).toString());
//            hitMap.put(DELIVERY_DESTINATION,
//                    hitDocument.getDocField().get(DELIVERY_DESTINATION).toString());
//            hitMap.put(POSTAL_CODE,
//                    hitDocument.getDocField().get(POSTAL_CODE).toString());
//            hitMap.put(ADDRESS_LINE,
//                    hitDocument.getDocField().get(ADDRESS_LINE).toString());
//            hitMap.put(PHONE,
//                    hitDocument.getDocField().get(PHONE).toString());
//            hitMap.put(FAX,
//                    hitDocument.getDocField().get(FAX).toString());
//            paramDataMap.put("data", hitMap);
//        }
//        return paramDataMap;
//    }
//
//    @Override
//    public Map<String, Object> checkSalesRepresentative(String searchKey) {
//        List<HitDocument> hitDocumentList = getDataFromElastic(searchKey,
//                Arrays.asList(SALES_REPRESENTATIVE));
//        Map<String, Object> paramDataMap = new HashMap<>();
//        paramDataMap.put(KEYWORD, hitDocumentList.isEmpty());
//        return paramDataMap;
//    }
//
//    @Override
//    public Map<String, Object> checkCustomerContact(String searchKey) {
//        List<String> customerNameList = new ArrayList<>();
//        customerNameList.add(CUSTOMER_CONTACT);
//        List<HitDocument> hitDocumentList = getDataFromElastic(searchKey,
//                customerNameList);
//        Map<String, Object> paramDataMap = new HashMap<>();
//        paramDataMap.put(KEYWORD, CollectionUtils.isEmpty(hitDocumentList));
//        return paramDataMap;
//    }
//
//    /**
//     * getDataFromElastic is used to get the data from the elastic table.
//     *
//     * @param searchKey
//     *            the search key
//     * @param fetchfieldList
//     *            the fetch field list
//     * @return List
//     */
//    private List<HitDocument> getDataFromElastic(String searchKey, List<String> fetchfieldList) {
//        AutocompleteEntity autocompleteEntity = new AutocompleteEntity();
//        autocompleteEntity.setPrefix(searchKey);
//        autocompleteEntity.setCaretPosition(INITIAL_VALUE);
//        List<AutocompleteTypeSetting> typeSettingList = new ArrayList<>();
//        AutocompleteTypeSetting typeSetting = new AutocompleteTypeSetting();
//        typeSetting.setType(SCM_COM_SALES_CUSTOMER_MASTER);
//        typeSetting.setSearchGroupName("ac");
//        typeSetting.setFetchFields(fetchfieldList);
//        typeSetting.setMapper(new HitDocumentResponseMapper());
//        typeSetting.setLanguage(userContext.getLocale());
//        typeSettingList.add(typeSetting);
//        Autocompleter autocompleter = autocompleterFactory.create(typeSettingList);
//        AutocompleteResult result = autocompleter.search(autocompleteEntity);
//        Map<String, Object> docMap = result.getHitDocs();
//        FullTextSearchResult hitList = (FullTextSearchResult)docMap
//                .get(SCM_COM_SALES_CUSTOMER_MASTER);
//        return hitList.getHits();
//    }
//
//    @Override
//    public IvtlAnchorListItemsVo getAnchorListItemsVo(List<SalesOrderFileUploader> internalDocumentList) {
//        List<SalesOrderFileUploader> filesList;
//        filesList = Objects.nonNull(internalDocumentList)
//                ? internalDocumentList
//                : new ArrayList<>();
//        IvtlAnchorListItemsVo itemsVo = new IvtlAnchorListItemsVo();
//        List<IvtlAnchorListItemVo> voItemList = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(filesList)) {
//            voItemList = filesList.stream().map(
//                    item ->
//                    new IvtlAnchorListItemVo.Builder()
//                            .anchor(new WapAnchorLabelVo.Builder().label(item.getFileName()).build())
//                            .fileId(item.getFileId())
//                            .size(item.getFileSize())
//                            .userId(item.getFileUploaderUserId())
//                            .timeUUID(item.getTimeUUID())
//                            .build()
//                    ).collect(Collectors.toList());
//        }
//        itemsVo.setChildren(voItemList);
//        return itemsVo;
//    }
//
//    @Override
//    public IvtlFileAttachmentListVo getFileUploadListItemsVo(List<SalesOrderFileUploader> salesOrderFileUploaderList) {
//        List<IvtlFileAttachmentListItem> ivtlFileAttachmentList = new ArrayList<>();
//        Map<String, Object> filePropertiesMap = new HashMap<>();
//        if (Objects.nonNull(salesOrderFileUploaderList)) {
//            ivtlFileAttachmentList = salesOrderFileUploaderList.stream().map(item -> {
//                filePropertiesMap.put("timeUUID", item.getTimeUUID());
//                filePropertiesMap.put("userId", item.getFileUploaderUserId());
//                return IvtlFileAttachmentListItem.builder()
//                        .name(item.getFileName())
//                        .id(item.getFileId())
//                        .size(item.getFileSize())
//                        .properties(filePropertiesMap)
//                        .build();
//            }).collect(Collectors.toList());
//
//        }
//        return IvtlFileAttachmentListVo.builder().items(ivtlFileAttachmentList).build();
//
//    }
//
//    @Override
//    public List<SalesOrderReceivedDetailLocal> setItemDesiredDeliveryDate(
//            List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList) {
//        if (CollectionUtils.isNotEmpty(salesOrderReceivedDetailList)) {
//            salesOrderReceivedDetailList.stream().forEach(detail ->
//                    detail.setItemDesiredDeliveryDate(null)
//                    );
//            return salesOrderReceivedDetailList;
//        } else {
//            return salesOrderReceivedDetailList;
//        }
//    }
//
//    @Override
//    public AtomicReference<BigDecimal> setTotalAmount(List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList) {
//        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>();
//        totalAmount.set(BigDecimal.ZERO);
//        String subTotal = textResourceManager.getText(TextId.of(SUBTOTAL));
//        if (CollectionUtils.isNotEmpty(salesOrderReceivedDetailList)) {
//            salesOrderReceivedDetailList.stream().filter(item -> !(item.getItemname().equals(subTotal)))
//                    .forEach(detail -> totalAmount.set(totalAmount.get().add(detail.getTotalAmount())));
//        }
//        return totalAmount;
//    }
//
//    @Override
//    public List<SalesOrderFileUploader> removeFileUploaderList(List<SalesOrderFileUploader> salesOrderFileUploaderList,
//            SalesIvtlFileDelete salesIvtlFileDelete) {
//        List<SalesOrderFileUploader> updatedSalesOrderFileUploaderList = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(salesOrderFileUploaderList)) {
//            salesOrderFileUploaderList.stream().map(mapper -> {
//                if (!(mapper.getFileName().equals(salesIvtlFileDelete.getFileName())))
//                {
//                    updatedSalesOrderFileUploaderList.add(mapper);
//                }
//                return updatedSalesOrderFileUploaderList;
//            }).collect(Collectors.toList());
//        }
//        return updatedSalesOrderFileUploaderList;
//    }
//
//    @Override
//    public void insertSalesOrderAppHstEntityToSalesEntity(SalesOrderAppHstEntity salesOrderAppHstEntity) {
//        salesOrderAppHstDao.insertSalesOrderEntity(salesOrderAppHstEntity);
//    }
//
//    @Override
//    public String getServiceUrl(AuthorityManager authorityManager, ServiceManager serviceManager,
//            ScmUserContext scmUserContext, String inputServiceDefId) {
//        Set<ServiceId> serviceIdsSet = authorityManager.getAccessibleServiceIds(scmUserContext.getActiveRoleIdList());
//        List<ServiceId> accessibleServiceIdsList = new ArrayList<>();
//        accessibleServiceIdsList.addAll(serviceManager
//                .getServiceCfgsByServiceDefId(ServiceDefId.valueOf(inputServiceDefId))
//                .stream()
//                .filter(serviceCfg -> serviceIdsSet.contains(serviceCfg.getServiceId()))
//                .map(serviceCfg -> serviceCfg.getServiceId())
//                .collect(Collectors.toList()));
//        return accessibleServiceIdsList.get(INITIAL_VALUE).toString();
//    }
//
//    @Override
//    public void deleteDraft(String appId) {
//        salesOrderAppHstDao.deleteRecordWithLatestRevision(appId, LONG_VALUE);
//    }
//
//    @Override
//    public SalesOrderSlipDiscount getRevisionNumber(String salesOrderApplicationId) {
//        List<Object> keyList = new ArrayList<>();
//        keyList.add(salesOrderApplicationId);
//        keyList.add(salesOrderAppHstDao.getSingleAppHstIndex(salesOrderApplicationId).getRevisionNumber());
//        return salesOrderAppHstDao.getSingle(keyList).getSalesOrderSlipDiscount();
//    }
//
//    @Override
//    public List<SalesOrderReceivedDetailLocal> setDesiredDeliveryTime(Map<String, String> desiredDeliveryTime,
//            List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailEmptyList) {
//        if (CollectionUtils.isNotEmpty(salesOrderReceivedDetailEmptyList)) {
//            salesOrderReceivedDetailEmptyList
//                    .stream()
//                    .map(detail -> {
//                        detail.setDetaildesiredDeliveryTimeFrom(desiredDeliveryTime.get("startTime")
//                                .substring(DELIVERY_TIME_SEPERATOR));
//                        detail.setDetaildesiredDeliveryTimeTo(desiredDeliveryTime.get("endTime")
//                                .substring(DELIVERY_TIME_SEPERATOR));
//                        return detail;
//                    }).collect(Collectors.toList());
//        }
//        return salesOrderReceivedDetailEmptyList;
//    }
//
//    @Override
//    public void setGridDiscount(List<SalesOrderReceivedDetailLocal> salesOrderReceivedDetailList, String applicationId) {
//        TextMap textMap = textResourceManager.getTexts(Arrays.asList(TextId.of(DISCOUNT), TextId.of(SUBTOTAL),
//                TextId.of(DISCOUNT_NAME_LABEL),
//                TextId.of(DISCOUNT_RATE_LABEL), TextId.of(DISCOUNT_AMOUNT_LABEL),
//                TextId.of(DISCOUNT_LABEL)));
//        SalesOrderAppHstIndexEntity salesOrderAppHstEntity = salesOrderAppHstDao.getSingleAppHstIndex(applicationId);
//        salesOrderReceivedDetailList
//                .stream()
//                .filter(salesdetail -> (salesdetail.getItemname().equals(textMap.get(DISCOUNT)) || salesdetail
//                        .getItemname().equals(textMap.get(SUBTOTAL))))
//                .forEach(detail -> {
//                    SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal = salesOrderAppHstEntity
//                            .getSalesOrderReceivedDetail().get(detail.getRowNumber() - FINAL_VALUE);
//                    setGridValue(detail, textMap, salesOrderReceivedDetailLocal);
//                });
//    }
//
//    /**
//     * setGridValue is used to get set the data to grid from table.
//     *
//     * @param detail
//     *            it is the item detail
//     * @param textMap
//     *            contains the text entry values
//     * @return salesOrderReceivedDetailLocal, detail used to check condition
//     */
//    private void setGridValue(SalesOrderReceivedDetailLocal detail, TextMap textMap,
//            SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal) {
//        if (detail.getItemname().equals(textMap.get(DISCOUNT))) {
//            SalesOrderDiscount salesOrderDiscount = salesOrderReceivedDetailLocal.getSalesOrderDiscount();
//            detail.setDiscountAmountLabel(textMap.get(DISCOUNT_AMOUNT_LABEL));
//            detail.setDiscountAmount(detail.getTotalAmount());
//            detail.setDiscountName(salesOrderDiscount.getDiscountDocumentName());
//            detail.setDiscountRateLabel(textMap.get(DISCOUNT_RATE_LABEL));
//            detail.setDiscountRate(salesOrderDiscount.getDiscountRate());
//            detail.setDiscountDetailType(salesOrderDiscount.getDiscountCalculationType());
//            detail.setDiscountType(salesOrderDiscount.getDiscountDetailType());
//            detail.setDiscountNameLabel(textMap.get(DISCOUNT_NAME_LABEL));
//        } else {
//            detail.setDiscountNameLabel(textMap.get(SUBTOTAL));
//        }
//    }
//
//    /**
//     * taxDivisioncaluculation is used to calculate the taxDivisionOperation in sales order grid.
//     *
//     * @param applicationEntity
//     *            pass the entity values
//     * @param salesOrderReceivedDetailLocal
//     *            pass the grid list values
//     *
//     */
//    private void taxDivisioncaluculation(ApplicationEntity<OrderEntryContents> applicationEntity,
//            SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal) throws TaxCalcException {
//        TaxCalculationEntity taxEntity = new TaxCalculationEntity();
//        if ((applicationEntity.getBusinessData().getCurrencyCode()).equals(scmUserContext.getCurrencyCode())) {
//            taxEntity.setCorpId(scmUserContext.getActiveCorpId());
//            taxEntity.setBaseDate(applicationEntity.getBusinessData().getOrderReceivedDate());
//            taxEntity.setCurrencyDiv(INITIAL_VALUE);
//            taxEntity.setCurrencyCode(applicationEntity.getBusinessData().getCurrencyCode());
//            taxEntity.setRowIndex(salesOrderReceivedDetailLocal.getRowNumber() - FINAL_VALUE);
//            taxEntity.setAmount(salesOrderReceivedDetailLocal.getTotalAmount());
//            taxEntity.setTeAmount(salesOrderReceivedDetailLocal.getTotalAmount());
//            taxEntity.setTaxInoutDiv(INITIAL_VALUE);
//            taxEntity.setNationCode(salesOrderReceivedDetailLocal.getNationCode());
//            taxEntity.setTaxdivCode(salesOrderReceivedDetailLocal.getTaxDivisionCode());
//            taxEntity.setTaxCalculationMode(TaxCalculationMode.CALCULATE_TAX);
//            taxCalculatorCoreService.calculateAndAdjust(taxEntity);
//            salesOrderReceivedDetailLocal.setTaxExcludeAmount(taxEntity.getTeAmount());
//            salesOrderReceivedDetailLocal.setTaxAmount(taxEntity.getTax());
//            salesOrderReceivedDetailLocal.setTotalAmount(taxEntity.getAmount());
//        } else {
//            taxEntity.setCorpId(scmUserContext.getActiveCorpId());
//            taxEntity.setBaseDate(applicationEntity.getBusinessData().getOrderReceivedDate());
//            taxEntity.setCurrencyDiv(FINAL_VALUE);
//            taxEntity.setCurrencyCode(applicationEntity.getBusinessData().getCurrencyCode());
//            taxEntity.setRowIndex(salesOrderReceivedDetailLocal.getRowNumber() - FINAL_VALUE);
//            taxEntity.setAmount(salesOrderReceivedDetailLocal.getTotalAmount());
//            taxEntity.setFAmount(salesOrderReceivedDetailLocal.getTotalAmount());
//            taxEntity.setTaxInoutDiv(FINAL_VALUE);
//            taxEntity.setNationCode(salesOrderReceivedDetailLocal.getNationCode());
//            taxEntity.setTaxdivCode(salesOrderReceivedDetailLocal.getTaxDivisionCode());
//            taxEntity.setTaxCalculationMode(TaxCalculationMode.CALCULATE_TAX);
//            taxCalculatorCoreService.calculateAndAdjust(taxEntity);
//            salesOrderReceivedDetailLocal.setTaxExcludeAmount(taxEntity.getTeAmount());
//            salesOrderReceivedDetailLocal.setTaxAmount(taxEntity.getTax());
//            salesOrderReceivedDetailLocal.setTotalAmount(taxEntity.getAmount());
//        }
//    }
//
//    /**
//     * convertionTaxCalculation is used to calculate the convert foreign amount to base amount calculatuion in sales
//     * order grid.
//     *
//     * @param applicationEntity
//     *            pass the entity values
//     * @param salesOrderReceivedDetailLocal
//     *            pass the grid list values
//     *
//     */
//    private void convertionTaxCalculation(ApplicationEntity<OrderEntryContents> applicationEntity,
//            SalesOrderReceivedDetailLocal salesOrderReceivedDetailLocal) {
//        ForeignExchangeResult foreignExchangeResult = null;
//        if (Objects.equals(applicationEntity.getBusinessData().getRateFix(), INITIAL_VALUE)) {
//            salesOrderReceivedDetailLocal.setConvertTaxExcludeAmount(BigDecimal.ZERO);
//            salesOrderReceivedDetailLocal.setConvertTaxAmount(BigDecimal.ZERO);
//            salesOrderReceivedDetailLocal.setConvertOrderAmount(BigDecimal.ZERO);
//        } else {
//            int rateFixAmount = applicationEntity.getBusinessData().getRateFix();
//            String rateTypeCode = applicationEntity.getBusinessData().getRateTypeCode();
//            if (Objects.nonNull(salesOrderReceivedDetailLocal
//                    .getTaxExcludeAmount()) && Objects.nonNull(salesOrderReceivedDetailLocal
//                    .getTaxAmount())) {
//                salesOrderReceivedDetailLocal.setConvertTaxExcludeAmount(salesOrderReceivedDetailLocal
//                        .getTaxExcludeAmount().multiply(BigDecimal.valueOf(rateFixAmount)));
//                salesOrderReceivedDetailLocal.setConvertTaxAmount(salesOrderReceivedDetailLocal
//                        .getTaxAmount().multiply(BigDecimal.valueOf(rateFixAmount)));
//                salesOrderReceivedDetailLocal.setConvertOrderAmount(salesOrderReceivedDetailLocal
//                        .getTotalAmount().multiply(BigDecimal.valueOf(rateFixAmount)));
//            }
//        }
//    }
//}