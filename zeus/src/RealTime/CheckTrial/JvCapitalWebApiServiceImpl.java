//package RealTime.CheckTrial;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.UUID;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//
//import lombok.RequiredArgsConstructor;
//import lombok.AutoIndex;
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.datastax.driver.core.exceptions.InvalidQueryException;
//import com.worksap.company.access.cassandra.exception.CassandraNoHostAvailableException;
//import com.worksap.company.access.cassandra.exception.CassandraQueryValidationException;
//import com.worksap.company.hue.scm.bizcore.project.dao.JvCapitaMonthlyApplicationDao;
//import com.worksap.company.hue.scm.bizcore.project.dao.JvCapitalMonthlyIdRelationDao;
//import com.worksap.company.hue.scm.bizcore.project.dao.JvCapitalOnDemandApplicationDao;
//import com.worksap.company.hue.scm.bizcore.project.dao.JvConfigurationMasterDao;
//import com.worksap.company.hue.scm.bizcore.project.dao.ProjectDao;
//import com.worksap.company.hue.scm.bizcore.project.entity.JvCapitaMonthlyApplicationEntity;
//import com.worksap.company.hue.scm.bizcore.project.entity.JvCapitalMonthlyIdRelationEntity;
//import com.worksap.company.hue.scm.bizcore.project.entity.JvCapitalOnDemandApplicationEntity;
//import com.worksap.company.hue.scm.bizcore.project.entity.JvConfigurationMasterEntity;
//import com.worksap.company.hue.scm.bizcore.project.entity.ProjectEntity;
//import com.worksap.company.hue.scm.type.project.JvCapitalAppHeaderValues;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.BillAppCollectMethodlValues;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.BillingApplicationItemVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.GetBillingAppItemsResponseVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.GetBillingAppItemsVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.GetJvBalanceResponseVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.GetJvBalanceVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.GetOnDemandAllocationAdditionalReportResponseVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.GetOnDemandAllocationAdditionalReportVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.JvCapitalAppDetailValues;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.PayAppApplicaitonDetailValues;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.PaymentApplicationItemResponseVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.PaymentApplicationItemVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.UpdateAppStatusApprovedVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.UpdateAppStatusResponseVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.UpdateAppStatusVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.UpdateOnDemandAppVo;
//import com.worksap.company.hue.scm.webapi.spec.project.vo.constructionbusinesssupport.jvcapitalmanagement.UpdatePrintStatusVo;
//
///**
// * JvCapitalWebApiServiceImpl is used to implements the methods of JvCapitalWebApiService
// *
// * @author Gowtham M
// * @since HUE 18.03
// */
//@Slf4j
//@AutoIndex(Kkk.class)
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class JvCapitalWebApiServiceImpl implements JvCapitalWebApiService {
//
//    /** The Constant ARGUMENT_INVALID. */
//    private static final String ARGUMENT_INVALID = "Argument is not specified";
//
//    /** The Constant UPDATE_FAILED. */
//    private static final String UPDATE_FAILED = "Could not update the JvCapitaMonthlyApplicationDto";
//
//    /** The Constant DEMAND_UPDATE_DBERROR. */
//    private static final String DEMAND_UPDATE_DBERROR = "Can not connect  to JvCapitalOnDemandApplicationDto";
//
//    /** The Constant RELATION_DBERROR. */
//    private static final String RELATION_DBERROR = "Can not connect  to JvCapitalMonthlyIdRelationDto";
//
//    /** The Constant APPLICATION_DBERROR. */
//    private static final String APPLICATION_DBERROR = "Can not connect to JvCapitalMonthlyApplicationDto";
//
//    /** The Constant APPLICATION_UPDATEERROR. */
//    private static final String APPLICATION_UPDATEERROR = "Can not update the JvCapitaMonthlyApplicationDto";
//
//    /** The Constant DEMAND_UPDATEERROR. */
//    private static final String DEMAND_UPDATEERROR = "Can not update the JvCapitalOnDemandApplicationDto";
//
//    /** The Constant APPLICATION_UPDATE_FAILED. */
//    private static final String APPLICATION_UPDATE_FAILED = "JvCapitaMonthlyApplicationDto can not be updated";
//
//    /** The Constant RECORD_UPDATE_FAILED. */
//    private static final String RECORD_UPDATE_FAILED = "The record status to be updated does not exist";
//
//    /** The Constant ARGUMENT_FAILED. */
//    private static final String ARGUMENT_FAILED = "The argument was invalid";
//
//    /** The Constant ATOMIC_INITIAL_INTEGER. */
//    private static final int ATOMIC_INITIAL_INTEGER = 0;
//
//    /** The Constant BILLING_CONDITON. */
//    private static final int BILLING_CONDITON = 1;
//
//    /** The Constant PAYMENT_CONDITION. */
//    private static final int PAYMENT_CONDITION = 2;
//
//    /** The Constant BILLING_LIST. */
//    private static final int BILLING_LIST = 2;
//
//    /** The Constant BIGDECIMAL_CONDITION. */
//    private static final int BIGDECIMAL_CONDITION = 500;
//
//    /** The Constant BSSEC_CONDITION. */
//    private static final long BSSEC_CONDITION = 0L;
//
//    /** The Constant APPLICATION_KIND. */
//    private static final int APPLICATION_KIND = 5;
//
//    /** The Variable jvCapitalMonthlyIdRelationDao */
//    private final JvCapitalMonthlyIdRelationDao jvCapitalMonthlyIdRelationDao;
//
//    /** The Variable jvCapitaMonthlyApplicationDao */
//    private final JvCapitaMonthlyApplicationDao jvCapitaMonthlyApplicationDao;
//
//    /** The Variable jvCapitalOnDemandApplicationDao */
//    private final JvCapitalOnDemandApplicationDao jvCapitalOnDemandApplicationDao;
//
//    /** The Variable jvConfigurationMasterDao */
//    private final JvConfigurationMasterDao jvConfigurationMasterDao;
//
//    /** The Variable projectDao */
//    private final ProjectDao projectDao;
//
//    @Override
//    public UpdateAppStatusResponseVo updateAppStatus(UpdateAppStatusVo updateAppStatusVo) throws IOException{
//        AtomicInteger iterator = new AtomicInteger(ATOMIC_INITIAL_INTEGER);
//        if (Objects.nonNull(updateAppStatusVo.getCorpId())
//                && Objects.nonNull(updateAppStatusVo.getApplicationIdList())
//                && Objects.nonNull(updateAppStatusVo.getJvCapitalMonthlyIdList())
//                && Objects.nonNull(updateAppStatusVo.getApplicationStatusList())) {
//            List<JvCapitalMonthlyIdRelationEntity> entityRelationList = new ArrayList<>();
//            List<JvCapitaMonthlyApplicationEntity> entityAppList = new ArrayList<>();
//            List<Object> checkRelationList = new ArrayList<>();
//            List<Object> checkAppList = new ArrayList<>();
//            updateAppStatusVo
//                    .getApplicationIdList()
//                    .stream()
//                    .forEach(
//                            getSingle -> {
//                                JvCapitalMonthlyIdRelationEntity jvCapitalMonthlyIdRelationEntity = new JvCapitalMonthlyIdRelationEntity();
//                                JvCapitaMonthlyApplicationEntity jvCapitaMonthlyApplicationEntity = new
//                                        JvCapitaMonthlyApplicationEntity();
//                                jvCapitalMonthlyIdRelationEntity.setCorpId(updateAppStatusVo.getCorpId());
//                                jvCapitaMonthlyApplicationEntity.setCorpId(updateAppStatusVo.getCorpId());
//                                jvCapitalMonthlyIdRelationEntity.setApplicationId(getSingle);
//                                jvCapitaMonthlyApplicationEntity.setApplicationId(getSingle);
//                                jvCapitalMonthlyIdRelationEntity.setJvCapitaMonthlyId(updateAppStatusVo
//                                        .getJvCapitalMonthlyIdList().get(iterator.get()));
//                                jvCapitaMonthlyApplicationEntity.setJvCapitaMonthlyId(updateAppStatusVo
//                                        .getJvCapitalMonthlyIdList().get(iterator.get()));
//                                entityRelationList.add(jvCapitalMonthlyIdRelationEntity);
//                                entityAppList.add(jvCapitaMonthlyApplicationEntity);
//                                checkRelationList.add(Arrays.asList(updateAppStatusVo.getCorpId(), getSingle));
//                                checkAppList.add(Arrays.asList(
//                                        updateAppStatusVo.getJvCapitalMonthlyIdList().get(iterator.get()),
//                                        updateAppStatusVo.getCorpId()));
//                                iterator.getAndIncrement();
//                            });
//            try {
//                List<JvCapitalMonthlyIdRelationEntity> getRelationList = jvCapitalMonthlyIdRelationDao
//                        .getMultiple(checkRelationList);
//                if (getRelationList.isEmpty()) {
//                    jvCapitalMonthlyIdRelationDao.insert(entityRelationList);
//                } else {
//                    List<JvCapitalMonthlyIdRelationEntity> sendRelationList = getRelationList.stream()
//                            .filter(action -> !entityRelationList.contains(action))
//                            .collect(Collectors.toList());
//                    if (Objects.nonNull(sendRelationList)) {
//                        jvCapitalMonthlyIdRelationDao.insert(sendRelationList);
//                    }
//                }
//            } catch (InvalidQueryException cassandraDataAccessException) {
//                log.error(RELATION_DBERROR, cassandraDataAccessException.toString());
//                return UpdateAppStatusResponseVo
//                        .builder().isSuccess(false).errorMessage(RELATION_DBERROR)
//                        .build();
//            }
//            return updateAppSubMethod(checkAppList, updateAppStatusVo);
//
//        } else {
//            log.error(ARGUMENT_INVALID, ARGUMENT_INVALID);
//            return UpdateAppStatusResponseVo
//                    .builder().isSuccess(false).errorMessage(ARGUMENT_INVALID).build();
//        }
//    }
//
//    /**
//     * updateAppSubMethod is used to get the data.
//     *
//     * @param checkAppList
//     * @param updateAppStatusVo
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo updateAppSubMethod(List<Object> checkAppList,
//            UpdateAppStatusVo updateAppStatusVo) {
//        try {
//            List<JvCapitaMonthlyApplicationEntity> getApplicationList = jvCapitaMonthlyApplicationDao
//                    .getMultiple(checkAppList);
//            if (Objects.nonNull(getApplicationList)) {
//                return updateStatusMethod(getApplicationList, updateAppStatusVo);
//
//            } else {
//                return UpdateAppStatusResponseVo
//                        .builder().isSuccess(false).errorMessage(ARGUMENT_FAILED)
//                        .build();
//            }
//        } catch (InvalidQueryException cassandraDataAccessException) {
//            log.error(APPLICATION_DBERROR, cassandraDataAccessException.toString());
//            return UpdateAppStatusResponseVo
//                    .builder().isSuccess(false).errorMessage(APPLICATION_DBERROR)
//                    .build();
//        }
//    }
//
//    /**
//     * updateStatusMethod is used to get the data from jvCapitaMonthlyApplicationDao.
//     *
//     * @param getAppilicationList
//     * @param updateAppStatusVo
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo updateStatusMethod(List<JvCapitaMonthlyApplicationEntity> getApplicationList,
//            UpdateAppStatusVo updateAppStatusVo) {
//        try {
//            return updateAppStatusSubMethod(getApplicationList, updateAppStatusVo);
//
//        } catch (CassandraQueryValidationException cassandraDataAccessException) {
//            log.error(UPDATE_FAILED, cassandraDataAccessException.toString());
//            return UpdateAppStatusResponseVo
//                    .builder().isSuccess(false).errorMessage(UPDATE_FAILED)
//                    .build();
//        }
//
//    }
//
//    /**
//     * updateAppStatusSubMethod is used to get the data from jvCapitaMonthlyApplicationDao.
//     *
//     * @param getAppilicationList
//     * @param updateAppStatusVo
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo updateAppStatusSubMethod(
//            List<JvCapitaMonthlyApplicationEntity> getAppilicationList,
//            UpdateAppStatusVo updateAppStatusVo) {
//        AtomicInteger atomicApp = new AtomicInteger(ATOMIC_INITIAL_INTEGER);
//        if ((getAppilicationList.size() == updateAppStatusVo
//                .getApplicationIdList().size())) {
//            List<JvCapitaMonthlyApplicationEntity> insertAppList = getAppilicationList.stream()
//                    .map(action -> {
//                        action.setApplicationStatus(updateAppStatusVo
//                                .getApplicationStatusList().get(atomicApp.get()));
//                        atomicApp.getAndIncrement();
//                        return action;
//                    }).collect(Collectors.toList());
//            jvCapitaMonthlyApplicationDao.insert(insertAppList);
//            return UpdateAppStatusResponseVo.builder()
//                    .isSuccess(true).build();
//        } else {
//            return UpdateAppStatusResponseVo
//                    .builder().isSuccess(false).errorMessage("The argument(JvCapitalMonthlyId)was invalid")
//                    .build();
//        }
//
//    }
//
//    @Override
//    public UpdateAppStatusResponseVo updatePrintStatus(UpdatePrintStatusVo updatePrintStatusVo) {
//
//        if (Objects.nonNull(updatePrintStatusVo.getCorpId())
//                && Objects.nonNull(updatePrintStatusVo.getApplicationIdList())
//                && Objects.nonNull(updatePrintStatusVo.getPrintStatusList())) {
//            try {
//                List<Object> checkRelationList = updatePrintStatusVo
//                        .getApplicationIdList()
//                        .stream()
//                        .map(
//                                data ->
//                                Arrays.asList(updatePrintStatusVo.getCorpId(), data)
//                        ).collect(Collectors.toList());
//                List<JvCapitalMonthlyIdRelationEntity> getRelationList = jvCapitalMonthlyIdRelationDao
//                        .getMultiple(checkRelationList);
//                return updatePrintStatusMethod(getRelationList,
//                        updatePrintStatusVo);
//            } catch (InvalidQueryException cassandraDataAccessException) {
//                log.error(RELATION_DBERROR, cassandraDataAccessException.toString());
//                return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                        .errorMessage(RELATION_DBERROR).build();
//            }
//        } else {
//            log.error(ARGUMENT_INVALID, ARGUMENT_INVALID);
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(ARGUMENT_INVALID).build();
//        }
//
//    }
//
//    /**
//     * updatePrintStatusMethod is used to get the data from jvCapitalOnDemandApplicationDao.
//     *
//     * @param getRelationList
//     * @param updatePrintStatusVo
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo updatePrintStatusMethod(List<JvCapitalMonthlyIdRelationEntity> getRelationList,
//            UpdatePrintStatusVo updatePrintStatusVo) {
//        List<Object> getDataList = new ArrayList<>();
//        try {
//            getDataList = updatePrintStatusVo
//                    .getApplicationIdList()
//                    .stream()
//                    .map(
//                            data ->
//                            Arrays.asList(data, updatePrintStatusVo.getCorpId())
//                    ).collect(Collectors.toList());
//            List<JvCapitalOnDemandApplicationEntity> getDemandList = jvCapitalOnDemandApplicationDao
//                    .getMultiple(getDataList);
//            return printStatusSubMethod(getRelationList, updatePrintStatusVo, getDemandList);
//        } catch (InvalidQueryException cassandraDataAccessException) {
//            log.error(DEMAND_UPDATE_DBERROR, cassandraDataAccessException.toString());
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(DEMAND_UPDATE_DBERROR).build();
//        }
//    }
//
//    /**
//     * printStatusSubMethod is used to update the printstatus data .
//     *
//     * @param getRelationList
//     * @param updatePrintStatusVo
//     * @param getDemandList
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo printStatusSubMethod(List<JvCapitalMonthlyIdRelationEntity> getRelationList,
//            UpdatePrintStatusVo updatePrintStatusVo,
//            List<JvCapitalOnDemandApplicationEntity> getDemandList) {
//        AtomicBoolean sizeFlag = new AtomicBoolean(false);
//        int checkValue = ATOMIC_INITIAL_INTEGER;
//        while (checkValue < getDemandList.size() && checkValue < getRelationList.size()) {
//            if (getDemandList.get(checkValue).getApplicationId().equals(getRelationList.get(checkValue)
//                    .getApplicationId())) {
//                sizeFlag.getAndSet(true);
//            }
//            checkValue++;
//        }
//        if ((getDemandList.size() == updatePrintStatusVo.getApplicationIdList().size() && getRelationList
//                .size() == updatePrintStatusVo.getApplicationIdList().size()) || sizeFlag.get()) {
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage("Duplicate data exists in the target DB")
//                    .build();
//        } else if ((getDemandList.isEmpty() && getRelationList
//                .isEmpty())) {
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(RECORD_UPDATE_FAILED)
//                    .build();
//        } else {
//            return printStatusConditionCheckMethod(getRelationList, updatePrintStatusVo, getDemandList);
//        }
//    }
//
//    /**
//     * printStatusConditionCheckMethod is used to update the printstatus data .
//     *
//     * @param getRelationList
//     * @param updatePrintStatusVo
//     * @param getDemandList
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo printStatusConditionCheckMethod(
//            List<JvCapitalMonthlyIdRelationEntity> getRelationList,
//            UpdatePrintStatusVo updatePrintStatusVo,
//            List<JvCapitalOnDemandApplicationEntity> getDemandList) {
//        AtomicInteger iteratorValue = new AtomicInteger(ATOMIC_INITIAL_INTEGER);
//        AtomicInteger iteratorDemand = new AtomicInteger(ATOMIC_INITIAL_INTEGER);
//        List<Object> getCheckList;
//        if (getRelationList.size() == updatePrintStatusVo.getApplicationIdList().size()) {
//            getCheckList = updatePrintStatusVo
//                    .getApplicationIdList()
//                    .stream()
//                    .map(
//                            data -> {
//                                List<Object> getArrayList = Arrays.asList(
//                                        getRelationList.get(iteratorValue.get())
//                                                .getJvCapitaMonthlyId(), updatePrintStatusVo.getCorpId());
//                                iteratorValue.getAndIncrement();
//                                return getArrayList;
//                            }).collect(Collectors.toList());
//            try {
//                List<JvCapitaMonthlyApplicationEntity> getAppList = jvCapitaMonthlyApplicationDao
//                        .getMultiple(getCheckList);
//                if (Objects.nonNull(getAppList)) {
//                    List<JvCapitaMonthlyApplicationEntity> insertApplicationList = getAppList.stream().map(
//                            action -> {
//                                action.setPrintStatus(updatePrintStatusVo.getPrintStatusList().get(
//                                        iteratorDemand.get()));
//                                iteratorDemand.getAndIncrement();
//                                return action;
//                            }).collect(Collectors.toList());
//                    return printStatusInsertMethod(insertApplicationList);
//                }
//            } catch (InvalidQueryException invalidQueryException) {
//                log.error(APPLICATION_UPDATEERROR, invalidQueryException.toString());
//                return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                        .errorMessage(APPLICATION_UPDATEERROR)
//                        .build();
//            }
//            return UpdateAppStatusResponseVo.builder().isSuccess(true)
//                    .build();
//        } else if (getDemandList.size() == updatePrintStatusVo.getApplicationIdList().size()) {
//            List<JvCapitalOnDemandApplicationEntity> insertDemandList = getDemandList.stream().map(
//                    action -> {
//                        action.setPrintStatus(updatePrintStatusVo.getPrintStatusList().get(
//                                iteratorDemand.get()));
//                        iteratorDemand.getAndIncrement();
//                        return action;
//                    }).collect(Collectors.toList());
//            return insertData(insertDemandList);
//        } else {
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(RECORD_UPDATE_FAILED)
//                    .build();
//        }
//    }
//
//    /**
//     * printStatusInsertMethod is used to update data .
//     *
//     * @param checkList
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo printStatusInsertMethod(List<JvCapitaMonthlyApplicationEntity> checkList) {
//        try {
//            jvCapitaMonthlyApplicationDao
//                    .insert(checkList);
//            return UpdateAppStatusResponseVo.builder().isSuccess(true)
//                    .build();
//
//        } catch (InvalidQueryException cassandraDataAccessException) {
//            log.error(APPLICATION_UPDATE_FAILED, cassandraDataAccessException.toString());
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(APPLICATION_UPDATE_FAILED)
//                    .build();
//        }
//
//    }
//
//    /**
//     * insertData is used to update data .
//     *
//     * @param checkDataList
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo insertData(List<JvCapitalOnDemandApplicationEntity> checkDataList) {
//        try {
//            jvCapitalOnDemandApplicationDao
//                    .insert(checkDataList);
//            return UpdateAppStatusResponseVo.builder().isSuccess(true)
//                    .build();
//        } catch (CassandraQueryValidationException cassandraDataAccessException) {
//            log.error(DEMAND_UPDATEERROR, cassandraDataAccessException.toString());
//            return UpdateAppStatusResponseVo.builder().isSuccess(false).errorMessage(DEMAND_UPDATEERROR)
//                    .build();
//        }
//
//    }
//
//    @Override
//    public GetBillingAppItemsResponseVo getBillingAppItems(GetBillingAppItemsVo getBillingAppItemsVo) {
//        Map<UUID, BillingApplicationItemVo> monthlyIdMap = new HashMap<>();
//        List<String> dataList = new ArrayList<>();
//        dataList.add("collectCondition");
//        dataList.add("billingCondition");
//        if (Objects.nonNull(getBillingAppItemsVo.getCorpId())
//                && Objects.nonNull(getBillingAppItemsVo.getJvCapitalMonthlyIdList())) {
//            try {
//                List<Object> masterKeyList = dataList
//                        .stream()
//                        .map(
//                                getData ->
//                                Arrays.asList(getData, getBillingAppItemsVo.getCorpId(), BSSEC_CONDITION)
//                        ).collect(Collectors.toList());
//
//                List<JvConfigurationMasterEntity> jvConfigurationMasterList = jvConfigurationMasterDao
//                        .getMultiple(masterKeyList);
//                return billingGetData(jvConfigurationMasterList, getBillingAppItemsVo, monthlyIdMap);
//
//            } catch (InvalidQueryException cassandraDataAccessException) {
//                log.error(APPLICATION_DBERROR, cassandraDataAccessException.toString());
//                return GetBillingAppItemsResponseVo.builder().isSuccess(false).errorMessage(APPLICATION_DBERROR)
//                        .build();
//            }
//        } else {
//            log.error(ARGUMENT_INVALID, ARGUMENT_INVALID);
//            return GetBillingAppItemsResponseVo.builder()
//                    .errorMessage(ARGUMENT_INVALID).isSuccess(false)
//                    .build();
//        }
//
//    }
//
//    /**
//     * billingGetData is used to check condition and fetch the data from table.
//     *
//     * @param jvConfigurationMasterList
//     * @param getBillingAppItemsVo
//     * @param flagCheck
//     * @param monthlyIdMap
//     * @return GetBillingAppItemsResponseVo
//     */
//    private GetBillingAppItemsResponseVo billingGetData(List<JvConfigurationMasterEntity> jvConfigurationMasterList,
//            GetBillingAppItemsVo getBillingAppItemsVo,
//            Map<UUID, BillingApplicationItemVo> monthlyIdMap) {
//        if (Objects.nonNull(jvConfigurationMasterList) && jvConfigurationMasterList.size() == BILLING_LIST) {
//            List<Object> keyList = getBillingAppItemsVo
//                    .getJvCapitalMonthlyIdList()
//                    .stream()
//                    .map(
//                            getData ->
//                            Arrays.asList(getData, getBillingAppItemsVo.getCorpId())
//                    ).collect(Collectors.toList());
//            if (Objects.nonNull(jvConfigurationMasterList.get(ATOMIC_INITIAL_INTEGER).getConfigurationKey())
//                    && Objects.nonNull(jvConfigurationMasterList.get(ATOMIC_INITIAL_INTEGER).getUUIDValue())
//                    && Objects.nonNull(jvConfigurationMasterList.get(BILLING_CONDITON).getConfigurationKey())
//                    && Objects.nonNull(jvConfigurationMasterList.get(BILLING_CONDITON).getUUIDValue())) {
//                return billingAppItemMethod(jvConfigurationMasterList, keyList,
//                        monthlyIdMap, getBillingAppItemsVo);
//            } else {
//                return GetBillingAppItemsResponseVo.builder()
//                        .errorMessage(ARGUMENT_FAILED).isSuccess(false)
//                        .billingApplicationItemMap(null).build();
//            }
//        } else {
//            return GetBillingAppItemsResponseVo.builder()
//                    .errorMessage(ARGUMENT_FAILED).isSuccess(false)
//                    .billingApplicationItemMap(null).build();
//        }
//
//    }
//
//    /**
//     * billingAppItemMethod is used to set the values from fetched data.
//     *
//     * @param jvConfigurationMasterList
//     * @param keyList
//     * @param flagCheck
//     * @param monthlyIdMap
//     * @param getBillingAppItemsVo
//     * @return GetBillingAppItemsResponseVo
//     */
//    private GetBillingAppItemsResponseVo billingAppItemMethod(
//            List<JvConfigurationMasterEntity> jvConfigurationMasterList,
//            List<Object> keyList,
//            Map<UUID, BillingApplicationItemVo> monthlyIdMap,
//            GetBillingAppItemsVo getBillingAppItemsVo) {
//        try {
//            int transactMethodIterator = ATOMIC_INITIAL_INTEGER;
//            int appDetailIterator = ATOMIC_INITIAL_INTEGER;
//            BigDecimal totalAmount = null;
//            List<JvCapitaMonthlyApplicationEntity> jvCapitaMonthlyApplicationEntityDetailList = jvCapitaMonthlyApplicationDao
//                    .getMultiple(keyList);
//            if (Objects.nonNull(jvCapitaMonthlyApplicationEntityDetailList)
//                    && (jvCapitaMonthlyApplicationEntityDetailList.size() == getBillingAppItemsVo
//                            .getJvCapitalMonthlyIdList()
//                            .size())) {
//                BillAppCollectMethodlValues billAppCollectMethodlValues = new BillAppCollectMethodlValues();
//                JvCapitalAppDetailValues jvCapitalAppDetailValues = new JvCapitalAppDetailValues();
//                List<BillAppCollectMethodlValues> billAppCollectMethodlValuesList = new ArrayList<>();
//                List<JvCapitalAppDetailValues> jvCapitalAppDetailValuesList = new ArrayList<>();
//
//                AtomicInteger loopIterator = new AtomicInteger(ATOMIC_INITIAL_INTEGER);
//                while (loopIterator.get() < jvCapitaMonthlyApplicationEntityDetailList.size()) {
//                    AtomicInteger transactMethodValue = new AtomicInteger(ATOMIC_INITIAL_INTEGER);
//                    AtomicInteger appDetailValue = new AtomicInteger(ATOMIC_INITIAL_INTEGER);
//                    if (Objects.nonNull(jvCapitaMonthlyApplicationEntityDetailList
//                            .get(loopIterator.get())
//                            .getTransactionMethodDetailValues())) {
//                        transactMethodIterator = jvCapitaMonthlyApplicationEntityDetailList
//                                .get(loopIterator.get())
//                                .getTransactionMethodDetailValues()
//                                .size();
//                    }
//                    if (Objects.nonNull(jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get())
//                            .getTransactionMethodDetailValues())) {
//                        appDetailIterator = jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get())
//                                .getTransactionMethodDetailValues()
//                                .size();
//                    }
//                    transactMethod(transactMethodValue, transactMethodIterator, billAppCollectMethodlValues,
//                            jvCapitaMonthlyApplicationEntityDetailList, billAppCollectMethodlValuesList);
//                    appDetailMethod(appDetailValue, appDetailIterator, jvCapitalAppDetailValues,
//                            jvCapitaMonthlyApplicationEntityDetailList, jvCapitalAppDetailValuesList);
//                    if (Objects.nonNull(jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get())
//                            .getJvCapitalAppHeaderValues())) {
//                        totalAmount = jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get())
//                                .getJvCapitalAppHeaderValues()
//                                .getTotalAmount();
//                    }
//
//                    BillingApplicationItemVo billingApplicationItemVo = BillingApplicationItemVo
//                            .builder()
//                            .corpId(jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get()).getCorpId())
//                            .bizPartnerId(
//                                    jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get())
//                                            .getPartnerCompanyId())
//                            .jvCapitalBilingAllocationId(
//                                    jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get())
//                                            .getJvCapitaMonthlyId())
//                            .totalAmount(totalAmount)
//                            .memoText(jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get()).getMemoText())
//                            .billingAllocationCalcDate(
//                                    jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get()).getCalcDate())
//                            .documentDate(
//                                    jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get())
//                                            .getDocumentDate())
//                            .reportIdList(
//                                    jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get())
//                                            .getAdditionalReportIdList())
//                            .collectConditionId(
//                                    jvConfigurationMasterList.get(ATOMIC_INITIAL_INTEGER)
//                                            .getUUIDValue())
//                            .billingConditionId(
//                                    jvConfigurationMasterList.get(BILLING_CONDITON)
//                                            .getUUIDValue())
//                            .billAppCollectMethodlValuesList(billAppCollectMethodlValuesList)
//                            .billAppDetailValuesList(jvCapitalAppDetailValuesList)
//                            .build();
//                    monthlyIdMap.put(jvCapitaMonthlyApplicationEntityDetailList.get(loopIterator.get())
//                            .getJvCapitaMonthlyId(),
//                            billingApplicationItemVo);
//                    loopIterator.getAndIncrement();
//                }
//                return GetBillingAppItemsResponseVo.builder().isSuccess(true).billingApplicationItemMap(monthlyIdMap)
//                        .build();
//            }
//            else {
//                return GetBillingAppItemsResponseVo.builder()
//                        .errorMessage(ARGUMENT_FAILED).isSuccess(false)
//                        .billingApplicationItemMap(null).build();
//            }
//        } catch (InvalidQueryException cassandraDataAccessException) {
//            log.error(APPLICATION_DBERROR, cassandraDataAccessException.toString());
//            return GetBillingAppItemsResponseVo.builder().isSuccess(false).errorMessage(APPLICATION_DBERROR)
//                    .build();
//        }
//
//    }
//
//    /**
//     * appDetailMethod is used to set the values.
//     *
//     * @param appDetailValue
//     * @param appDetailIterator
//     * @param jvCapitalAppDetailValues
//     * @param jvCapitaMonthlyApplicationEntityDetailList
//     * @param jvCapitalAppDetailValuesList
//     */
//    private void appDetailMethod(AtomicInteger appDetailValue, int appDetailIterator,
//            JvCapitalAppDetailValues jvCapitalAppDetailValues,
//            List<JvCapitaMonthlyApplicationEntity> jvCapitaMonthlyApplicationEntityDetailList,
//            List<JvCapitalAppDetailValues> jvCapitalAppDetailValuesList) {
//        if (appDetailValue.get() < appDetailIterator) {
//            jvCapitalAppDetailValues
//                    .setJvCapitalBillingAllocationDetailId(jvCapitaMonthlyApplicationEntityDetailList
//                            .get(appDetailValue.get())
//                            .getJvCapitalAppDetailValues().get(appDetailValue.get())
//                            .getJvCapitalBillingAllocationDetailId());
//            jvCapitalAppDetailValues
//                    .setCollectMethodNumber(jvCapitaMonthlyApplicationEntityDetailList
//                            .get(appDetailValue.get())
//                            .getJvCapitalAppDetailValues().get(appDetailValue.get())
//                            .getCollectMethodNumber());
//            jvCapitalAppDetailValues
//                    .setDetailAmount(jvCapitaMonthlyApplicationEntityDetailList.get(appDetailValue.get())
//                            .getJvCapitalAppDetailValues().get(appDetailValue.get())
//                            .getDetailAmount());
//            jvCapitalAppDetailValues
//                    .setDescription(jvCapitaMonthlyApplicationEntityDetailList.get(appDetailValue.get())
//                            .getJvCapitalAppDetailValues().get(appDetailValue.get())
//                            .getDescription());
//            jvCapitalAppDetailValuesList.add(jvCapitalAppDetailValues);
//            appDetailValue.getAndIncrement();
//        }
//
//    }
//
//    /**
//     * transactMethod is used to set the values.
//     *
//     * @param transactMethodValue
//     * @param transactMethodIterator
//     * @param billAppCollectMethodlValues
//     * @param jvCapitaMonthlyApplicationEntityDetailList
//     * @param billAppCollectMethodlValuesList
//     */
//    private void transactMethod(AtomicInteger transactMethodValue, int transactMethodIterator,
//            BillAppCollectMethodlValues billAppCollectMethodlValues,
//            List<JvCapitaMonthlyApplicationEntity> jvCapitaMonthlyApplicationEntityDetailList,
//            List<BillAppCollectMethodlValues> billAppCollectMethodlValuesList) {
//        if (transactMethodValue.get() < transactMethodIterator) {
//            billAppCollectMethodlValues
//                    .setCollectMethodNumber(jvCapitaMonthlyApplicationEntityDetailList
//                            .get(transactMethodValue.get())
//                            .getTransactionMethodDetailValues().get(transactMethodValue.get())
//                            .getTransactionMethodNumber());
//            billAppCollectMethodlValues
//                    .setCollectmethod(jvCapitaMonthlyApplicationEntityDetailList
//                            .get(transactMethodValue.get())
//                            .getTransactionMethodDetailValues().get(transactMethodValue.get())
//                            .getTransactionMethodType());
//            billAppCollectMethodlValues.setAmount(jvCapitaMonthlyApplicationEntityDetailList
//                    .get(transactMethodValue.get())
//                    .getTransactionMethodDetailValues().get(transactMethodValue.get())
//                    .getTransactionAmount());
//            billAppCollectMethodlValues
//                    .setSettlementDate(jvCapitaMonthlyApplicationEntityDetailList
//                            .get(transactMethodValue.get())
//                            .getTransactionMethodDetailValues().get(transactMethodValue.get())
//                            .getBillApprovalDate());
//            billAppCollectMethodlValuesList.add(billAppCollectMethodlValues);
//            transactMethodValue.getAndIncrement();
//        }
//
//    }
//
//    @Override
//    public PaymentApplicationItemResponseVo getPaymentAppItems(GetBillingAppItemsVo getBillingAppItemsVo) {
//        Map<UUID, PaymentApplicationItemVo> monthlyIdMap = new HashMap<>();
//        if (Objects.nonNull(getBillingAppItemsVo.getCorpId())
//                && Objects.nonNull(getBillingAppItemsVo.getJvCapitalMonthlyIdList())) {
//            try {
//                JvConfigurationMasterEntity jvConfigurationMasterEntity = jvConfigurationMasterDao.getSingle(
//                        "paymentConditionId", getBillingAppItemsVo.getCorpId(), BSSEC_CONDITION);
//                if (Objects.isNull(jvConfigurationMasterEntity)) {
//                    return PaymentApplicationItemResponseVo.builder()
//                            .errorMessage(ARGUMENT_FAILED).isSuccess(false)
//                            .payAppDetailValuesMap(null).build();
//                } else {
//                    if (Objects.nonNull(jvConfigurationMasterEntity.getConfigurationKey())
//                            && Objects.nonNull(jvConfigurationMasterEntity.getUUIDValue())) {
//
//                        List<Object> keyList = getBillingAppItemsVo
//                                .getJvCapitalMonthlyIdList()
//                                .stream()
//                                .map(
//                                        getData ->
//                                        Arrays.asList(getData, getBillingAppItemsVo.getCorpId())
//                                ).collect(Collectors.toList());
//                        return getPaymentAppMethod(keyList, jvConfigurationMasterEntity, monthlyIdMap,
//                                getBillingAppItemsVo);
//                    } else {
//                        return PaymentApplicationItemResponseVo.builder()
//                                .errorMessage(ARGUMENT_FAILED).isSuccess(false)
//                                .payAppDetailValuesMap(null).build();
//                    }
//                }
//            } catch (InvalidQueryException cassandraDataAccessException) {
//                log.error(APPLICATION_DBERROR, cassandraDataAccessException.toString());
//                return PaymentApplicationItemResponseVo.builder().isSuccess(false).errorMessage(APPLICATION_DBERROR)
//                        .build();
//            }
//        } else {
//            log.error(ARGUMENT_INVALID, ARGUMENT_INVALID);
//            return PaymentApplicationItemResponseVo.builder()
//                    .errorMessage(ARGUMENT_INVALID).isSuccess(false)
//                    .build();
//        }
//    }
//
//    /**
//     * getPaymentAppMethod is used to get the data .
//     *
//     * @param keyList
//     * @param jvConfigurationMasterEntity
//     * @param monthlyIdMap
//     * @param flagCheck
//     * @param getBillingAppItemsVo
//     * @return PaymentApplicationItemResponseVo
//     */
//    private PaymentApplicationItemResponseVo getPaymentAppMethod(List<Object> keyList,
//            JvConfigurationMasterEntity jvConfigurationMasterEntity,
//            Map<UUID, PaymentApplicationItemVo> monthlyIdMap,
//            GetBillingAppItemsVo getBillingAppItemsVo) {
//        int listIterator = ATOMIC_INITIAL_INTEGER;
//        BigDecimal totalAmount = null;
//        try {
//            List<JvCapitaMonthlyApplicationEntity> jvCapitaMonthlyApplicationEntityList = jvCapitaMonthlyApplicationDao
//                    .getMultiple(keyList);
//            if (Objects.nonNull(jvCapitaMonthlyApplicationEntityList)
//                    && (jvCapitaMonthlyApplicationEntityList.size() == getBillingAppItemsVo.getJvCapitalMonthlyIdList()
//                            .size())) {
//                List<PayAppApplicaitonDetailValues> payAppApplicaitonDetailValuesList = new ArrayList<>();
//
//                AtomicInteger itemValue = new AtomicInteger(ATOMIC_INITIAL_INTEGER);
//                if (Objects.nonNull(jvCapitaMonthlyApplicationEntityList.get(itemValue.get())
//                        .getJvCapitalAppHeaderValues())) {
//                    totalAmount = jvCapitaMonthlyApplicationEntityList.get(itemValue.get())
//                            .getJvCapitalAppHeaderValues()
//                            .getTotalAmount();
//                }
//                while (itemValue.get() < jvCapitaMonthlyApplicationEntityList.size()) {
//                    AtomicInteger detailValue = new AtomicInteger(ATOMIC_INITIAL_INTEGER);
//                    if (Objects.nonNull(jvCapitaMonthlyApplicationEntityList.get(itemValue.get())
//                            .getTransactionMethodDetailValues())) {
//                        listIterator = jvCapitaMonthlyApplicationEntityList.get(itemValue.get())
//                                .getTransactionMethodDetailValues()
//                                .size();
//                    }
//                    PayAppApplicaitonDetailValues payAppApplicaitonDetailValues = new PayAppApplicaitonDetailValues();
//                    detailMethod(detailValue, listIterator, payAppApplicaitonDetailValues,
//                            payAppApplicaitonDetailValuesList, jvCapitaMonthlyApplicationEntityList);
//
//                    PaymentApplicationItemVo paymentApplicationItemVo = PaymentApplicationItemVo
//                            .builder()
//                            .corpId(jvCapitaMonthlyApplicationEntityList.get(itemValue.get()).getCorpId())
//                            .bizPartnerId(
//                                    jvCapitaMonthlyApplicationEntityList.get(itemValue.get()).getPartnerCompanyId())
//                            .jvCapitalBilingAllocationId(
//                                    jvCapitaMonthlyApplicationEntityList.get(itemValue.get()).getJvCapitaMonthlyId())
//                            .totalAmount(totalAmount)
//                            .memoText(jvCapitaMonthlyApplicationEntityList.get(itemValue.get()).getMemoText())
//                            .billingAllocationCalcDate(
//                                    jvCapitaMonthlyApplicationEntityList.get(itemValue.get()).getCalcDate())
//                            .documentDate(jvCapitaMonthlyApplicationEntityList.get(itemValue.get()).getDocumentDate())
//                            .reportIdList(
//                                    jvCapitaMonthlyApplicationEntityList.get(itemValue.get())
//                                            .getAdditionalReportIdList())
//                            .paymentConditionId(
//                                    jvConfigurationMasterEntity
//                                            .getUUIDValue())
//                            .payAppDetailValuesList(payAppApplicaitonDetailValuesList)
//                            .build();
//                    monthlyIdMap.put(jvCapitaMonthlyApplicationEntityList.get(itemValue.get()).getJvCapitaMonthlyId(),
//                            paymentApplicationItemVo);
//                    itemValue.getAndIncrement();
//                }
//                return PaymentApplicationItemResponseVo.builder().isSuccess(true).payAppDetailValuesMap(monthlyIdMap)
//                        .build();
//            } else {
//                return PaymentApplicationItemResponseVo.builder()
//                        .errorMessage(ARGUMENT_FAILED).isSuccess(false)
//                        .payAppDetailValuesMap(null).build();
//            }
//        } catch (InvalidQueryException cassandraDataAccessException) {
//            log.error(APPLICATION_DBERROR, cassandraDataAccessException.toString());
//            return PaymentApplicationItemResponseVo.builder().isSuccess(false).errorMessage(APPLICATION_DBERROR)
//                    .build();
//        }
//
//    }
//
//    /**
//     * detailMethod is used to get the data .
//     *
//     * @param detailValue
//     * @param listIterator
//     * @param payAppApplicaitonDetailValues
//     * @param payAppApplicaitonDetailValuesList
//     * @param jvCapitaMonthlyApplicationEntityList
//     */
//    private void detailMethod(AtomicInteger detailValue, int listIterator,
//            PayAppApplicaitonDetailValues payAppApplicaitonDetailValues,
//            List<PayAppApplicaitonDetailValues> payAppApplicaitonDetailValuesList,
//            List<JvCapitaMonthlyApplicationEntity> jvCapitaMonthlyApplicationEntityList) {
//        if (detailValue.get() < listIterator) {
//            payAppApplicaitonDetailValues
//                    .setJvCapitalBillingAllocationDetailId(jvCapitaMonthlyApplicationEntityList
//                            .get(detailValue.get())
//                            .getJvCapitalAppDetailValues().get(detailValue.get())
//                            .getJvCapitalBillingAllocationDetailId());
//            payAppApplicaitonDetailValues
//                    .setDetailAmount(jvCapitaMonthlyApplicationEntityList.get(detailValue.get())
//                            .getJvCapitalAppDetailValues().get(detailValue.get())
//                            .getDetailAmount());
//
//            payAppApplicaitonDetailValues
//                    .setDescription(jvCapitaMonthlyApplicationEntityList.get(detailValue.get())
//                            .getJvCapitalAppDetailValues().get(detailValue.get())
//                            .getDescription());
//
//            payAppApplicaitonDetailValuesList.add(payAppApplicaitonDetailValues);
//            detailValue.getAndIncrement();
//        }
//
//    }
//
//    @Override
//    public UpdateAppStatusResponseVo updateOnDemandApp(UpdateOnDemandAppVo updateOnDemandAppVo) {
//        if (Objects.nonNull(updateOnDemandAppVo.getCorpId())
//                && Objects.nonNull(updateOnDemandAppVo.getApplicationId())
//                && Objects.nonNull(updateOnDemandAppVo.getApplicationStatus())
//                && Objects.nonNull(updateOnDemandAppVo.getBssecId())
//                && Objects.nonNull(updateOnDemandAppVo.getJvCapitalAppHeaderValues())) {
//            try {
//                JvCapitalOnDemandApplicationEntity jvCapitalOnDemandApplicationEntity = jvCapitalOnDemandApplicationDao
//                        .getSingle(updateOnDemandAppVo.getApplicationId(),
//                                updateOnDemandAppVo.getCorpId());
//                if (Objects.isNull(jvCapitalOnDemandApplicationEntity)) {
//                    return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                            .errorMessage("failed to insert the list of JvCapitalOnDemand Application").build();
//                } else {
//                    JvCapitalAppHeaderValues jvCapitalAppHeaderValues = jvCapitalOnDemandApplicationEntity
//                            .getJvCapitalAppHeaderValues();
//                    jvCapitalAppHeaderValues.setNoteAmount(updateOnDemandAppVo.getJvCapitalAppHeaderValues()
//                            .getNoteAmount());
//                    jvCapitalAppHeaderValues.setPartnerCompanyName(updateOnDemandAppVo.getJvCapitalAppHeaderValues()
//                            .getPartnerCompanyName());
//                    jvCapitalAppHeaderValues.setProjectName(updateOnDemandAppVo.getJvCapitalAppHeaderValues()
//                            .getProjectName());
//                    jvCapitalAppHeaderValues.setTotalAmount(updateOnDemandAppVo.getJvCapitalAppHeaderValues()
//                            .getTotalAmount());
//                    jvCapitalOnDemandApplicationEntity.setApplicationId(updateOnDemandAppVo.getApplicationId());
//                    jvCapitalOnDemandApplicationEntity.setCorpId(updateOnDemandAppVo.getCorpId());
//                    jvCapitalOnDemandApplicationEntity.setBssecId(updateOnDemandAppVo.getBssecId());
//                    jvCapitalOnDemandApplicationEntity.setApplicationKind(APPLICATION_KIND);
//                    jvCapitalOnDemandApplicationEntity.setPrintStatus(ATOMIC_INITIAL_INTEGER);
//                    jvCapitalOnDemandApplicationEntity.setApplicationStatus(updateOnDemandAppVo.getApplicationStatus());
//                    jvCapitalOnDemandApplicationEntity.setTimestamp(LocalDate.now());
//                    jvCapitalOnDemandApplicationEntity.setJvCapitalAppHeaderValues(jvCapitalAppHeaderValues);
//                    return updateDemandMethod(jvCapitalOnDemandApplicationEntity);
//
//                }
//            } catch (InvalidQueryException cassandraException) {
//                log.error(DEMAND_UPDATE_DBERROR, cassandraException.toString());
//                return UpdateAppStatusResponseVo.builder().isSuccess(false).errorMessage(DEMAND_UPDATE_DBERROR)
//                        .build();
//            }
//        } else {
//            log.error(ARGUMENT_INVALID, ARGUMENT_INVALID);
//            return UpdateAppStatusResponseVo.builder().isSuccess(false).errorMessage(ARGUMENT_INVALID).build();
//        }
//    }
//
//    /**
//     * updateDemandMethod is used to update data .
//     *
//     * @param jvCapitalOnDemandApplicationEntity
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo updateDemandMethod(
//            JvCapitalOnDemandApplicationEntity jvCapitalOnDemandApplicationEntity) {
//        try {
//            jvCapitalOnDemandApplicationDao.insert(jvCapitalOnDemandApplicationEntity);
//            return UpdateAppStatusResponseVo.builder().isSuccess(true).build();
//        } catch (InvalidQueryException cassandraException) {
//            log.error(DEMAND_UPDATE_DBERROR, cassandraException.toString());
//            return UpdateAppStatusResponseVo.builder().isSuccess(false).errorMessage(DEMAND_UPDATE_DBERROR)
//                    .build();
//        }
//
//    }
//
//    @Override
//    public GetOnDemandAllocationAdditionalReportResponseVo getOnDemandAllocationAdditionalReport(
//            GetOnDemandAllocationAdditionalReportVo getOnDemandAllocationAdditionalReportVo) {
//        if (Objects.nonNull(getOnDemandAllocationAdditionalReportVo.getCorpId())
//                && Objects.nonNull(getOnDemandAllocationAdditionalReportVo.getProjectId())
//                && Objects.nonNull(getOnDemandAllocationAdditionalReportVo.getBizPartnerId())
//                && Objects.nonNull(getOnDemandAllocationAdditionalReportVo.getAllocationAmount())) {
//            return GetOnDemandAllocationAdditionalReportResponseVo.builder().isSuccess(true)
//                    .additionalReportKeyList(Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()))
//                    .build();
//        } else {
//            log.error(ARGUMENT_INVALID, ARGUMENT_INVALID);
//            return GetOnDemandAllocationAdditionalReportResponseVo.builder().isSuccess(false)
//                    .errorMessage(ARGUMENT_INVALID).build();
//        }
//    }
//
//    @Override
//    public GetJvBalanceResponseVo getJvBalance(GetJvBalanceVo getJvBalanceVo) {
//        Map<UUID, BigDecimal> balanceAmountMap = new HashMap<>();
//        if (Objects.nonNull(getJvBalanceVo.getCorpId())
//                && Objects.nonNull(getJvBalanceVo.getProjectId())) {
//            ProjectEntity projectEntity = projectDao.getSingle(Arrays.asList(
//                    getJvBalanceVo.getProjectId(), getJvBalanceVo.getCorpId()));
//            if (Objects.nonNull(projectEntity)) {
//                projectEntity.getJvValues().getJvDetailValuesList().stream().forEach(action ->
//                        balanceAmountMap.put(action.getBizPartnerId(),
//                                BigDecimal.valueOf(BIGDECIMAL_CONDITION))
//                        );
//                return GetJvBalanceResponseVo.builder().isSuccess(true).balanceAmountMap(balanceAmountMap)
//                        .build();
//            } else {
//                log.error(ARGUMENT_INVALID, ARGUMENT_INVALID);
//                return GetJvBalanceResponseVo.builder().isSuccess(false)
//                        .errorMessage(ARGUMENT_INVALID).build();
//            }
//        } else {
//            log.error(ARGUMENT_INVALID, ARGUMENT_INVALID);
//            return GetJvBalanceResponseVo.builder().isSuccess(false)
//                    .errorMessage(ARGUMENT_INVALID).build();
//        }
//    }
//
//    @Override
//    public UpdateAppStatusResponseVo updateAppStatusApproved(UpdateAppStatusApprovedVo updateAppStatusApprovedVo) {
//        if (Objects.nonNull(updateAppStatusApprovedVo.getCorpId())
//                && Objects.nonNull(updateAppStatusApprovedVo.getApplicationId())) {
//            try {
//                JvCapitalMonthlyIdRelationEntity jvCapitalMonthlyIdRelationEntity =
//                        jvCapitalMonthlyIdRelationDao.getSingle(updateAppStatusApprovedVo.getCorpId(),
//                                updateAppStatusApprovedVo.getApplicationId());
//                return updateAppStatusMethod(updateAppStatusApprovedVo, jvCapitalMonthlyIdRelationEntity);
//            } catch (InvalidQueryException cassandraDataAccessException) {
//                log.error(RELATION_DBERROR, cassandraDataAccessException.toString());
//                return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                        .errorMessage(RELATION_DBERROR).build();
//            }
//        } else {
//            log.error(ARGUMENT_INVALID, ARGUMENT_INVALID);
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(ARGUMENT_INVALID).build();
//        }
//    }
//
//    /**
//     * updateAppStatusMethod is used to check the condition .
//     *
//     * @param updateAppStatusApprovedVo
//     * @param jvCapitalMonthlyIdRelationEntity
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo updateAppStatusMethod(UpdateAppStatusApprovedVo updateAppStatusApprovedVo,
//            JvCapitalMonthlyIdRelationEntity jvCapitalMonthlyIdRelationEntity) {
//        try {
//            JvCapitalOnDemandApplicationEntity jvCapitalOnDemandApplicationEntity = jvCapitalOnDemandApplicationDao
//                    .getSingle(updateAppStatusApprovedVo.getApplicationId(),
//                            updateAppStatusApprovedVo.getCorpId());
//            if ((Objects.nonNull(jvCapitalOnDemandApplicationEntity)
//            && Objects.nonNull(jvCapitalMonthlyIdRelationEntity))) {
//                return UpdateAppStatusResponseVo
//                        .builder()
//                        .isSuccess(false)
//                        .errorMessage(
//                                "The argument was invalid.There are duplicate registrations on the JvCapitalMonthlyIdRelationDto and JvCapitalOnDemandApplicationDto.")
//                        .build();
//
//            } else if (Objects.isNull(jvCapitalOnDemandApplicationEntity)
//                    && Objects.isNull(jvCapitalMonthlyIdRelationEntity)) {
//                return UpdateAppStatusResponseVo
//                        .builder()
//                        .isSuccess(false)
//                        .errorMessage(
//                                "The argument was invalid.There is no data on the JvCapitalMonthlyIdRelationDto and JvCapitalOnDemandApplicationDto.")
//                        .build();
//            } else {
//                return updateApproveMethod(jvCapitalMonthlyIdRelationEntity, jvCapitalOnDemandApplicationEntity);
//            }
//        } catch (CassandraNoHostAvailableException cassandraDataAccessException) {
//            log.error(DEMAND_UPDATEERROR, cassandraDataAccessException.toString());
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(DEMAND_UPDATEERROR).build();
//        }
//
//    }
//
//    /**
//     * updateApproveMethod is used to check the condition and update data .
//     *
//     * @param jvCapitalMonthlyIdRelationEntity
//     * @param jvCapitalOnDemandApplicationEntity
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo updateApproveMethod(
//            JvCapitalMonthlyIdRelationEntity jvCapitalMonthlyIdRelationEntity,
//            JvCapitalOnDemandApplicationEntity jvCapitalOnDemandApplicationEntity) {
//        try {
//            if (Objects.nonNull(jvCapitalMonthlyIdRelationEntity)) {
//                JvCapitaMonthlyApplicationEntity jvCapitaMonthlyApplicationEntity = jvCapitaMonthlyApplicationDao
//                        .getSingle(
//                                jvCapitalMonthlyIdRelationEntity.getJvCapitaMonthlyId(),
//                                jvCapitalMonthlyIdRelationEntity.getCorpId());
//                if (Objects.nonNull(jvCapitaMonthlyApplicationEntity)) {
//                    jvCapitaMonthlyApplicationEntity.setPaymentPlanStatus(PAYMENT_CONDITION);
//                    return updateAppStatusInsert(jvCapitaMonthlyApplicationEntity);
//                }
//            } else {
//                jvCapitalOnDemandApplicationEntity.setPaymentPlanStatus(PAYMENT_CONDITION);
//                return insertOnDemandData(jvCapitalOnDemandApplicationEntity);
//            }
//        } catch (InvalidQueryException invalidQueryException) {
//            log.error(APPLICATION_UPDATEERROR, invalidQueryException.toString());
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(APPLICATION_UPDATEERROR).build();
//        }
//        return UpdateAppStatusResponseVo.builder().isSuccess(true)
//                .build();
//
//    }
//
//    /**
//     * updateAppStatusInsert is used to update data .
//     *
//     * @param jvCapitaMonthlyApplicationEntity
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo updateAppStatusInsert(
//            JvCapitaMonthlyApplicationEntity jvCapitaMonthlyApplicationEntity) {
//        try {
//            jvCapitaMonthlyApplicationDao
//                    .insert(jvCapitaMonthlyApplicationEntity);
//            return UpdateAppStatusResponseVo.builder().isSuccess(true)
//                    .build();
//        } catch (InvalidQueryException cassandraDataAccessException) {
//            log.error(APPLICATION_UPDATE_FAILED, cassandraDataAccessException.toString());
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(APPLICATION_UPDATE_FAILED)
//                    .build();
//        }
//
//    }
//
//    /**
//     * insertOnDemandData is used to update data .
//     *
//     * @param jvCapitalOnDemandApplicationEntity
//     * @return UpdateAppStatusResponseVo
//     */
//    private UpdateAppStatusResponseVo insertOnDemandData(
//            JvCapitalOnDemandApplicationEntity jvCapitalOnDemandApplicationEntity) {
//        try {
//            jvCapitalOnDemandApplicationDao
//                    .insert(jvCapitalOnDemandApplicationEntity);
//            return UpdateAppStatusResponseVo.builder().isSuccess(true)
//                    .build();
//        } catch (CassandraQueryValidationException cassandraDataAccessException) {
//            log.error(DEMAND_UPDATEERROR, cassandraDataAccessException.toString());
//            return UpdateAppStatusResponseVo.builder().isSuccess(false)
//                    .errorMessage(DEMAND_UPDATEERROR)
//                    .build();
//        }
//
//    }
//}