goog.provide('wap.ac.payment.dwmt.withdrawalmanagement.portal.container.ActualWithdrawalPortalContainer');

goog.require('goog.Uri');
goog.require('goog.string');
goog.require('goog.string.StringBuffer');
goog.require('goog.window');
goog.require('wap.ac.payment.dwmt.withdrawalmanagement.portal.dao.WithdrawalDataRequest');
goog.require('wap.ac.payment.dwmt.withdrawalmanagement.portal.page.WithdrawalPortalPageRequest');
goog.require('wap.core.app.View');
goog.require('wap.core.common');
goog.require('wap.core.ui.SidemenuMenuList.EventType');
goog.require('wap.core.ui.WapDropdownButton.EventType');
goog.require('wap.core.ui.container.StandardContainer');
goog.require('wap.core.ui.inbox.WapInboxHoverMenu.EventType');
goog.require('wap.core.ui.inbox.WapInboxList.EventType');
goog.require('wap.core.util.style');




/**
 * ActualWithdrawalPortalContainer handles all the events and actions of the components in actual withdrawal portal screen.
 * 
 * @constructor
 * @extends {wap.core.ui.container.StandardContainer}
 * @author Barath Vignesh G
 */
wap.ac.payment.dwmt.withdrawalmanagement.portal.container.ActualWithdrawalPortalContainer = function() {
  goog.base(this);
};
goog.inherits(wap.ac.payment.dwmt.withdrawalmanagement.portal.container.ActualWithdrawalPortalContainer,
  wap.core.ui.container.StandardContainer);

/**
 * registerComponent method is used to register the name of this Js to the deps.js.
 */
wap.core.app.View.registerComponent(
  'wap.ac.payment.dwmt.withdrawalmanagement.portal.container.ActualWithdrawalPortalContainer',
  wap.ac.payment.dwmt.withdrawalmanagement.portal.container.ActualWithdrawalPortalContainer);

/**
 * Scope declaration,
 * Scope determines the accessibility (visibility) of variables and functions. 
 */
goog.scope(function() {

  var withdrawalPortalMainContainer = wap.ac.payment.dwmt.withdrawalmanagement.portal.container.ActualWithdrawalPortalContainer;
  var withdrawalPortalPage = new wap.ac.payment.dwmt.withdrawalmanagement.portal.page.WithdrawalPortalPageRequest();
  var withdrawalPortalRequest = new wap.ac.payment.dwmt.withdrawalmanagement.portal.dao.WithdrawalDataRequest();
  /**
   * COMPONENT_IDS_ is Enum used to store the componentIds
   * @private
   * @type{ SIDE_MENU_LIST: String, DASHBOARD_SIDEMENU_CONTENT: String, UNCONFIRMED_SIDEMENU_CONTENT: String, ACTUAL_WITHDRAWAL_CONTENT: String}
   * @type{ DROP_DOWN_CONTENT: String, INBOX_LIST: String, WAP_STATS_LABEL: String, EMPTY_DATA: String}
   * @type{ HOLD_SIDEMENU_CONTENT: String, DONE_SIDEMENU_CONTENT: String, MENU_ID: String}
   */
  withdrawalPortalMainContainer.COMPONENT_IDS_ = {
    SIDE_MENU_LIST: 'sidemenu-menu-list',
    DASHBOARD_SIDEMENU_CONTENT: 'dashboard-sidemenu-menu-list-simple-content',
    UNCONFIRMED_SIDEMENU_CONTENT: 'unconfirmed-sidemenu-menu-list-accordion-content',
    ACTUAL_WITHDRAWAL_SIDEMENU_CONTENT: 'actual-withdrawal-sidemenu-menu-list-accordion-content',
    DROP_DOWN_CONTENT: 'actual-withdrawal-dropdown-button',
    INBOX_LIST: 'actual-withdrawal-inbox-list',
    WAP_STATS_LABEL: 'actual-count-wap-stats-label',
    EMPTY_DATA: 'empty-data-row',
    HOLD_SIDEMENU_CONTENT: 'hold-sidemenu-menu-list-accordion-content',
    DONE_SIDEMENU_CONTENT: 'done-sidemenu-menu-list-accordion-content',
    MENU_ID: 'actual-menu-id-text-label'
  };

  /**
   * CONSTANTS_ is enum used to store the constants used in this JavaScript.
   * @private
   * @type {INDEX_VALUE: String, UNCONFIRMED_TOKEN: String, LOAD_UNCONFIRMED_PAGE: String}
   * @type{ LOAD_CONFIRM_INBOX: String, ACTUAL_WITHDRAWAL_PAGE: String, ACTUAL_WITHDRAWAL_TOKEN: String, POST: String}
   * @type{ LOAD_SORTED_INBOX: String, SORTED_VALUE: String, DETAIL: String, REQUEST_MAPPING: String}
   * @type{ DIVIDER: String, PAYMENT_ACCOUNT_STATEMENT: String, DETAIL_NAME: String, ASCENDING_ORDER_TEXT: String}
   * @type{ SORT_ASC: String, SORT_DESC: String, SORT_ORDER: String, DATA: String}
   * @type{ SERVICE_ID: String, ADDED_COUNT: int, TRANSFER_WITHDRAWAL_METHOD: String, DONE_WITHDRAWAL_PAGE: String}
   * @type{ HOLD_WITHDRAWAL_PAGE: String, SERVICE_DEF_ID: String, SERVICE_URL: String, SERVICE_ID_VALUE: String}
   */
  withdrawalPortalMainContainer.CONSTANTS_ = {
    INDEX_VALUE: 'withdrawalManagementIndex',
    UNCONFIRMED_TOKEN: 'unconfirmed',
    LOAD_UNCONFIRMED_PAGE: 'loadUnconfirmedPage',
    LOAD_CONFIRM_INBOX: 'loadUnConfirmInbox',
    ACTUAL_WITHDRAWAL_PAGE: 'loadActualWithdrawalPage',
    ACTUAL_WITHDRAWAL_TOKEN: 'actualWithdrawal',
    POST: 'POST',
    LOAD_SORTED_INBOX: 'loadSortedInbox',
    SORTED_VALUE: 'sortedValue',
    DETAIL: 'detail',
    REQUEST_MAPPING: '/hue/ac/payment/portal/',
    DIVIDER: '/',
    PAYMENT_ACCOUNT_STATEMENT: 'paymentAccountStatementId',
    DETAIL_NAME: 'detailName',
    ASCENDING_ORDER_TEXT: 'ascendingOrderText',
    SORT_ASC: '0',
    SORT_DESC: '1',
    SORT_ORDER: 'sortOrder',
    DATA: 'data',
    SERVICE_ID: '?sid=',
    ADDED_COUNT: 1,
    TRANSFER_WITHDRAWAL_METHOD: '/insertWithdrawalDetails?sid=',
    DONE_WITHDRAWAL_PAGE: 'loadDonePage',
    HOLD_WITHDRAWAL_PAGE: 'loadOnHoldPage',
    SERVICE_DEF_ID: 'serviceDefId',
    SERVICE_URL: 'serviceUrlValue',
    SERVICE_ID_VALUE: 'serviceIdValue'
  };

  /**
   * EVENT_ENUM_is enum used for storing event Types of the various components present in the screen.
   * @private
   * @type {string}
   */
  withdrawalPortalMainContainer.EVENT_ENUM_ = {
    INBOX_CLICK_ROW: wap.core.ui.inbox.WapInboxList.EventType.CLICK_ROW,
    SIDE_MENU_CHANGE: wap.core.ui.SidemenuMenuList.EventType.SIDE_MENU_CHANGE,
    DROP_DOWN_ACTION: wap.core.ui.WapDropdownButton.EventType.ACTION,
    EVENT_BEFORE_FETCH: wap.core.ui.inbox.WapInboxList.EventType.BEFORE_FETCH,
    BEFORE_HOVER_MENU_OPEN: wap.core.ui.inbox.WapInboxHoverMenu.EventType.BEFORE_OPEN,
    HOVER_CLICK: wap.core.ui.inbox.WapInboxList.EventType.CLICK_HOVER_MENU
  };

  /**
   * decorateInternal_ method initializes the component
   * 
   * @private
   */
  withdrawalPortalMainContainer.prototype.decorateInternal_ = function() {
    var idEnum = withdrawalPortalMainContainer.COMPONENT_IDS_;
    /**
     * @type {wap.core.app.View} Object
     * @private
     */
    this.viewInstance_ = wap.core.app.View.getInstance();
    /**
     * @type {goog.ui.Component}
     * @private
     */
    this.menuId_ = this.viewInstance_.getComponent(idEnum.MENU_ID);
    /**
     * @type {goog.ui.Component}
     * @private
     */
    this.sideMenuList_ = this.viewInstance_.getComponent(idEnum.SIDE_MENU_LIST);
    /**
     * @type {goog.ui.Component}
     * @private
     */
    this.dropDownList_ = this.viewInstance_.getComponent(idEnum.DROP_DOWN_CONTENT);
    /**
     * @type {goog.ui.Component}
     * @private
     */
    this.inboxList_ = this.viewInstance_.getComponent(idEnum.INBOX_LIST);
    /**
     * @type {goog.ui.Component}
     * @private
     */
    if (this.menuId_.getLabel() === 'unregistered') {
      /**
       * @type {goog.ui.Component}
       * @private
       */
      this.sideMenuList_.setActiveContent(idEnum.ACTUAL_WITHDRAWAL_SIDEMENU_CONTENT);
    }
    if (this.menuId_.getLabel() === 'done') {
      /**
       * @type {goog.ui.Component}
       * @private
       */
      this.sideMenuList_.setActiveContent(idEnum.DONE_SIDEMENU_CONTENT);
    }
    /**
     * @type {goog.ui.Component}
     * @private
     */
    this.statsLabel_ = this.viewInstance_.getComponent(idEnum.WAP_STATS_LABEL);
    /**
     * @type {goog.ui.Component}
     * @private
     */
    this.emptyDataRow_ = this.viewInstance_.getComponent(idEnum.EMPTY_DATA);
  };

  /**
   * listen_ method is to handle the events for the components.
   * 
   * @private
   * @param {Object}
   *            element
   * @param {String}
   *            event
   * @param {Object}
   *            functionValue
   */
  withdrawalPortalMainContainer.prototype.listen_ = function(element, event, functionValue) {
    var handler_ = this.getHandler();
    handler_.listen(element, event, functionValue);
  };

  /**
   * enterDocument binds events with its action.
   * @override
   */
  withdrawalPortalMainContainer.prototype.enterDocument = function() {
    goog.base(this, 'enterDocument');
    this.decorateInternal_();
    if (goog.isDefAndNotNull(this.sortOrder)) {
      this.sortOrder;
    } else {
      this.sortOrder = withdrawalPortalMainContainer.CONSTANTS_.SORT_ASC;
    }
    this.eventType = withdrawalPortalMainContainer.EVENT_ENUM_;
    this.listen_(this.sideMenuList_, this.eventType.SIDE_MENU_CHANGE, this.onClickSideMenu_);
    this.listen_(this.dropDownList_, this.eventType.DROP_DOWN_ACTION, this.loadSortedInbox_);
    this.listen_(this.inboxList_, this.eventType.EVENT_BEFORE_FETCH, this.beforeFetchData_);
    this.listen_(this.inboxList_, this.eventType.INBOX_CLICK_ROW, this.loadDetailScreen_);
    this.listen_(this.inboxList_, this.eventType.BEFORE_HOVER_MENU_OPEN, this.getDataToTransferWithdrawal_);
    this.getEmptyDataRow_(this.inboxList_, this.emptyDataRow_);
  };

  /**
   * getEmptyDataRow_ method is used to check initial condition for get empty data row in portal screen.
   * @param {Object} inboxListId
   * @param {Object} emptyDataRow
   * @private
   */
  withdrawalPortalMainContainer.prototype.getEmptyDataRow_ = function(inboxListId, emptyDataRow) {
    this.totalRows_ = inboxListId.getRowCounts();
    if (!this.totalRows_) {
      this.showRow_(emptyDataRow.getElement(), true);
    } else {
      this.showRow_(emptyDataRow.getElement(), false);
    }
  };

  /**
   * showRow_ method for showing search icon class 
   * if there is no data
   * @private
   * @param {String} element
   * @param {boolean} show           
   */
  withdrawalPortalMainContainer.prototype.showRow_ = function(element, show) {
    if (goog.isDefAndNotNull(element)) {
      if (show) {
        wap.core.util.style.setElementShown(element, show);
        this.dropDownList_.setDisplayed(false);
      } else {
        wap.core.util.style.setElementShown(element, false);
        this.dropDownList_.setDisplayed(true);
      }
    }
  };

  /**
   * loadDetailScreen_ method is to load the detail screen.
   * 
   * @param {goog.events.BrowserEvent} event
   * @private
   */
  withdrawalPortalMainContainer.prototype.loadDetailScreen_ = function(event) {
    var dataValueMap = {};
    var sortOrder;
    var constantValue = withdrawalPortalMainContainer.CONSTANTS_;
    var activeRow = event.target.getAdditionalData();
    var key = event.target.getKey();
    var presentRowCount = event.currentTarget.getRowIndexByKey(key) + constantValue.ADDED_COUNT;
    var totalRowCount = this.statsLabel_.getValue();
    var detailName = 'actualConfirm';
    dataValueMap[constantValue.PAYMENT_ACCOUNT_STATEMENT] = activeRow.get(constantValue.PAYMENT_ACCOUNT_STATEMENT);
    dataValueMap[constantValue.DETAIL_NAME] = detailName;
    dataValueMap['presentRowCount'] = presentRowCount;
    dataValueMap['totalRowCount'] = totalRowCount;
    dataValueMap[constantValue.SERVICE_DEF_ID] = 'PaymentManagementDetail';
    dataValueMap['screenKey'] = this.menuId_.getLabel();
    if (goog.isDefAndNotNull(this.sortOrder)) {
      sortOrder = this.sortOrder;
    } else {
      sortOrder = constantValue.SORT_ASC;
    }
    dataValueMap[constantValue.SORTED_VALUE] = sortOrder;
    var getRequest = withdrawalPortalRequest.doPromiseAjax(dataValueMap);
    getRequest.getResult().then(function(response) {
      var controllerMapping = response.getResponseJson();
      var serviceUrlValue = controllerMapping[constantValue.SERVICE_URL];
      var serviceIdValue = controllerMapping[constantValue.SERVICE_ID_VALUE];
      var url = new goog.string.StringBuffer(wap.core.common.getContextPath(),
        constantValue.DIVIDER, serviceUrlValue, constantValue.SERVICE_ID, serviceIdValue);
      goog.window.open(url.toString(), {
        'target': '_self'
      });
    });
  };

  /**
   * beforeFetchData_ method is to fetch the data before lazy load.
   * 
   * @private
   */
  withdrawalPortalMainContainer.prototype.beforeFetchData_ = function() {
    var sendFetchParam = {};
    var sortOrder;
    if (goog.isDefAndNotNull(this.sortOrder)) {
      sortOrder = this.sortOrder;
    } else {
      sortOrder = withdrawalPortalMainContainer.CONSTANTS_.SORT_ASC;
    }
    sendFetchParam[withdrawalPortalMainContainer.CONSTANTS_.SORT_ORDER] = sortOrder;
    this.inboxList_.setQueryForFetch(sendFetchParam);
  };

  /**
   * loadSortedInbox_ method is to sort the inbox.
   * 
   * @param {goog.events.BrowserEvent} event
   * @private
   */
  withdrawalPortalMainContainer.prototype.loadSortedInbox_ = function(event) {
    var url = goog.Uri.parse(withdrawalPortalMainContainer.CONSTANTS_.LOAD_SORTED_INBOX);
    var sortedKey = {};
    this.dropDownList_.setLabel(event[withdrawalPortalMainContainer.CONSTANTS_.DATA]['label']);
    this.sortOrder = event[withdrawalPortalMainContainer.CONSTANTS_.DATA]['value'];
    sortedKey[withdrawalPortalMainContainer.CONSTANTS_.SORT_ORDER] = this.sortOrder;
    sortedKey['keyword'] = this.sideMenuList_.getActiveContentId();
    withdrawalPortalPage.doPartialUpdate(url.toString(), sortedKey, true).then(goog.bind(function() {
      this.decorateInternal_();
      this.enterDocument();
    }, this));
  };

  /**
   * onClickSideMenu_ handles the side-menu event in portal.
   * @param {Event} event
   * @private
   */
  withdrawalPortalMainContainer.prototype.onClickSideMenu_ = function(event) {
    var idEnum = withdrawalPortalMainContainer.COMPONENT_IDS_;
    var activeTargetId = event[withdrawalPortalMainContainer.CONSTANTS_.DATA]['origin']['currentTarget']['id'];
    switch (activeTargetId) {
      case idEnum.DASHBOARD_SIDEMENU_CONTENT:
        this.dispatchToApp_(withdrawalPortalMainContainer.CONSTANTS_.INDEX_VALUE);
        break;
      case idEnum.UNCONFIRMED_SIDEMENU_CONTENT:
        this.dispatchToApp_(withdrawalPortalMainContainer.CONSTANTS_.LOAD_UNCONFIRMED_PAGE);
        break;
      case idEnum.HOLD_SIDEMENU_CONTENT:
        this.dispatchToApp_(withdrawalPortalMainContainer.CONSTANTS_.HOLD_WITHDRAWAL_PAGE);
        break;
      case idEnum.ACTUAL_WITHDRAWAL_SIDEMENU_CONTENT:
        this.dispatchToApp_(withdrawalPortalMainContainer.CONSTANTS_.ACTUAL_WITHDRAWAL_PAGE);
        break;
      case idEnum.DONE_SIDEMENU_CONTENT:
        this.dispatchToApp_(withdrawalPortalMainContainer.CONSTANTS_.DONE_WITHDRAWAL_PAGE);
        break;
      default:
        /** Since side Menu click determines the Case ,default is not called */
        break;
    }
  };

  /**
   * dispatchToApp_ method for updating screen values using single page application.
   * 
   * @private
   * @param {String} token
   */
  withdrawalPortalMainContainer.prototype.dispatchToApp_ = function(token) {
    this.url_ = goog.Uri.parse(new goog.string.StringBuffer(wap.core.common.getContextPath(),
      withdrawalPortalMainContainer.CONSTANTS_.REQUEST_MAPPING, token));
    goog.window.open(this.url_.toString(), {
      'target': '_self'
    });
  };

  /**
   * getDataToTransferWithdrawal_ method for updating screen values using unique uuid.
   * 
   * @private
   * @param {String} event
   */
  withdrawalPortalMainContainer.prototype.getDataToTransferWithdrawal_ = function(event) {
    var param = {};
    var constantValue = withdrawalPortalMainContainer.CONSTANTS_;
    var inboxListActiveRowKey = event['data']['rowKey'];
    var activeRow = this.inboxList_.getRowByKey(inboxListActiveRowKey);
    var additionalDetails = activeRow.getAdditionalData();
    param[constantValue.SERVICE_DEF_ID] = 'TransferWithdrawalRegistration';
    param[constantValue.PAYMENT_ACCOUNT_STATEMENT] = additionalDetails.get(constantValue.PAYMENT_ACCOUNT_STATEMENT);
    this.listen_(this.inboxList_, this.eventType.HOVER_CLICK, function() {
      this.loadTransferWithdrawalPage_(param);
    });
  };

  /**
   * loadTransferWithdrawalPage_ method loads TransferWithdrawalScreen
   * 
   * @private
   * @param {String} param
   */
  withdrawalPortalMainContainer.prototype.loadTransferWithdrawalPage_ = function(param) {
    var constantValue = withdrawalPortalMainContainer.CONSTANTS_;
    var getRequest = withdrawalPortalRequest.doPromiseAjaxScreenTransfer(param);
    getRequest.getResult().then(function(response) {
      var controllerMapping = response.getResponseJson();
      var serviceUrlValue = '/hue/ac/payment/regist/transfer';
      var serviceIdValue = controllerMapping[constantValue.SERVICE_ID_VALUE];
      var url = goog.string.buildString(wap.core.common.getContextPath(),
        constantValue.DIVIDER, serviceUrlValue, constantValue.TRANSFER_WITHDRAWAL_METHOD,
        serviceIdValue);
      var transferUrl = goog.Uri.parse(url);
      var selectedPaymentAccountStatementValueMap = {
        'valueDate': controllerMapping['withdrawalDate'],
        'bankAccountName': controllerMapping['paymentSourceAccountName'],
        'withdrawalAmount': controllerMapping['amountOfMoney'],
        'memo': controllerMapping['contents']
      };
      transferUrl.setParameterValue('paymentAccountStatementValues', JSON.stringify(
        selectedPaymentAccountStatementValueMap));
      goog.window.open(transferUrl.toString(), {
        'target': '_self'
      });
    });
  };

});
