goog.provide('wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.CustomerForecastDetail');

goog.require('goog.dom');
goog.require('wap.core.app.View');
goog.require('wap.core.net.ServiceLink');
goog.require('wap.core.txt.TextResource');
goog.require('wap.core.ui.WapButton.EventType');
goog.require('wap.core.ui.WapConfirmationDialog.EventType');
goog.require('wap.core.ui.WapDropdownButton.EventType');
goog.require('wap.core.ui.container.StandardContainer');
goog.require('wap.core.ui.inbox.WapInboxList.EventType');
goog.require('wap.core.ui.sima.Sheet.EventType');
goog.require('wap.ri.core.InputFormModelHelper');
goog.require('wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.dao.CustomerForecastDetailDao');
goog.require('wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.page.CustomerForecastDetailPage');
goog.require('wap.scm.sales.customerforecastmanagement.customerforecastmanagement.common.ScmSalesCommon');

/**
 * CustomerForecastDetail main Fuction
 * @constructor
 * @extends {wap.core.ui.container.StandardContainer}
 * @author Santthan1
 * @since HUE 17.11
 */
wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.CustomerForecastDetail = function() {
	goog.base(this);
};
goog.inherits(wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.CustomerForecastDetail,
		wap.core.ui.container.StandardContainer);
/**
 * Registering Constructor of CustomerForecastDetail.
 */
wap.core.app.View.registerComponent(
		'wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.CustomerForecastDetail',
		wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.CustomerForecastDetail);

/**
 * scope declaration
 * 
 */
goog.scope(function() {
	var customerForecastDetail =
		wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.CustomerForecastDetail;
	var customerForecastDetailPage =
		wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.page.CustomerForecastDetailPage;
	var customerForecastDetailDao =
		wap.scm.sales.customerforecastmanagement.customerforecast.customerdetail.dao.CustomerForecastDetailDao;
	/**
	 *EVENT_ENUM_ is the Enum for ID.
	 * 
	 * @private
	 * @enum {string}
	 */
	customerForecastDetail.EVENT_ENUM_ = {
			BUTTON_ACTION: wap.core.ui.WapButton.EventType.ACTION,
			DROP_DOWN_BUTTON_ACTION: wap.core.ui.WapDropdownButton.EventType.ACTION,
			CELL_EDIT: wap.core.ui.sima.Sheet.EventType.CELL_EDITING,
			CELL_CHANGE: wap.core.ui.sima.Sheet.EventType.CELLS_CHANGED,
			DRAG_CHANGE: wap.core.ui.sima.Sheet.EventType.DRAG,
			PASTE_CHANGE: wap.core.ui.sima.Sheet.EventType.PASTE,
			BEGIN_CELL_EDITING: wap.core.ui.sima.Sheet.EventType.BEGIN_CELL_EDITING,
			INBOX_CLICK_ROW: wap.core.ui.inbox.WapInboxList.EventType.CLICK_ROW,
			SAVE_CONFIRMATION_DIALOG_YES_ACTION: wap.core.ui.WapConfirmationDialog.EventType.OK
	};


	/**
	 * CONSTANTS_ENUM_ is an Enum for constants.
	 * 
	 * @private
	 * @enum {String}
	 */
	customerForecastDetail.CONSTANTS_ENUM_ = {
			DETAIL_ROOM: 'expand-grid-div-detail-apartment-latest',
			INHOUSE_CONTROLLER_URL: '/hue/scm/sales/customerforecastmanagement/customerforecastmanagement/inhouseforecastdetail/index?sid=',
			CONTROLLER_URL: '/hue/scm/sales/customerforecastmanagement/customerforecastmanagement/customerforecastdetail/',
			PORTAL_URL: '/hue/scm/sales/customerforecastmanagement/customerforecastmanagement/portal/customerforecastmanagement/',
			CUSTOMER_INQUIRY: 'loadCustomerInquiryInboxList?sid=ScmSaCustomerForecast.CustomerDetail',
			CUSTOMER_ID: '&customerId=',
			EMPTY: '',
			COMPARE_ZERO_INDEX: '0',
			NEXT: 'next',
			PREVIOUS: 'previous',
			INHOUSE_FORECAST_CYCLE_ID_APPEND: '&inhouseForecastCycleId=',
			COMPARE_DROP_DOWN_CLICK: 'compareDropDownClick',
			INDEX_APPEND: 'index',
			CUSTOMER_FORECAST_CYCLE_ID_APPEND: '&customerForecastCycleId=',
			VIEW_MODE: 'view',
			ASSIGN_ZERO_VALUE: 0,
			SECOND_COLUMN: 2,
			SUM_INDEX: 4,
			CHANGE_MODE_INDEX_APPEND: 'changeModeIndex?sid=',
			SAVED_SUCESSFULLY: 'SALE.CUFM.detail.save.sucessfully',
			SAVED_UNSUCESSFULLY: 'SALE.CUFM.detail.save.unsucessfully',
			EDIT_NOTICE_DROP_DOWN_ITEM: 'edit-notice-dropdown-item',
			VIEW_NOTICE_DROP_DOWN_ITEM: 'view-notice-dropdown-item',
			CURRENT_DATE: 'CurrentDate',
			MODE_EDIT: '&mode=edit',
			EDIT: 'edit',
			SUM: 'sum',
			MODE_CHANGE_METHOD: 'getModeValue',
			NAVIGATION_BUTTON_CLICK: 'navigationButtonClick',
			INHOUSE_TOASTR_MESSAGE: 'SALE.CUFM.detail.no.inhouse.data.toastr',
			ITEM_DUPLICATE_MESSAGE: 'SALE.CUFM.detail.item.duplicate'


	};

	/**
	 * getComponent_ method for getting the component for the given argument from the view instance.
	 * 
	 * @private
	 * @param {String} componentString
	 * @return {Object} componentObject
	 */
	customerForecastDetail.prototype.getComponent_ = function(componentString) {
		var componentObject = this.viewInstance_.getComponent(componentString);
		return componentObject;
	};


	/**
	 * listen_ method for updating partial update values and performing after function of partial updating.
	 * 
	 * @private
	 * @param {Object} element
	 * @param {String} event
	 * @param {Object} functionValue
	 */
	customerForecastDetail.prototype.listen_ = function(element, event, functionValue) {
		var handler_ = this.getHandler();
		handler_.listen(element, event, functionValue);
	};

	/**
	 * enterDocument binds event with its action.
	 * 
	 * @override
	 */
	customerForecastDetail.prototype.enterDocument = function() {
		goog.base(this, 'enterDocument');
		this.viewInstance_ = wap.core.app.View.getInstance();
		this.initialize_();
		this.eventHandlers_();
	};
	/**
	 * ID_ENUM_ is the Enum for ID.
	 * 
	 * @private
	 * @enum {string}
	 */
	customerForecastDetail.ID_ENUMS_ = {
			COMMNET_BUTTON: 'comment-btn',
			ALERT_BUTTON: 'alert-btn',
			BACK_TO_PORTAL_BUTTON: 'back-to-portl-button',
			GRID_COMPARE_DROP_DOWN_BUTTON: 'grid-compare-dropdown-button',
			RANGE_DROPDOWN_BUTTON: 'range-dropdown-button',
			INPUT_CONTROL_DROPDOWN_BUTTON: 'input-control-dropdown-button',
			SAVE_BUTTON: 'save-button',
			SEARCH_BUTTON: 'search-btn',
			SETTING_BUTTON: 'setting-control-btn',
			EXPAND_BUTTON: 'expand-button',
			CONTRACT_BUTTON: 'contract-button',
			DETAIL_GRID: 'detailed-notice-grid',
			PREVIOUS_BUTTON: 'previous-button',
			HISTORY_BUTTON: 'history-dropdown-button',
			NEXT_BUTTON: 'next-button',
			INQUIRY_LINK_LABEL: 'inquiry-link-anchor-label',
			WAP_TOASTR: 'save-toastr',
			INHOUSE_DIALOG: 'inhouse-list-dialog',
			INHOUSE_INBOX_LIST: 'inhouse-indication-inbox-list',
			BACK_TO_VIEW_BUTTON: 'back-to-view-button',
			ADD_BUTTON: 'add-button',
			CUSTOMER_ID: 'customer-id-hidden',
			CUSTOMER_DETAIL_INFO: 'customer-detail-info-hidden',
			CUSTOMER_SERVICE_DETAIL: 'customer-service-detail-hidden',
			BUTTON_DIV: 'button-l:row',
			GRID_ROW: 'grid-l:row',
			SAVE_CONFIRMATION_DIALOG: 'save-confirmation-dialog',
			BACK_TO_INQUIRY: 'back-to-inquiry-button'

	};

	/**
	 * initialize_ method for initializing all variables that are to be used in other methods.
	 * 
	 * @private
	 */
	customerForecastDetail.prototype.initialize_ = function() {
		this.servicelink_ = new wap.core.net.ServiceLink();
		var idEnums = customerForecastDetail.ID_ENUMS_;
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		this.data = {};
		var customerDetail = goog.dom.getElement(idEnums.CUSTOMER_DETAIL_INFO);
		var customDetailValue = customerDetail.value;
		var customerDetailInfo = JSON.parse(customDetailValue);
		if (customerDetailInfo === constants.EMPTY) {
			this.data['historyValue'] = constants.EMPTY;
			this.data['compareStatus'] = constants.COMPARE_ZERO_INDEX;
			this.data['mode'] = constants.VIEW_MODE;
		} else {
			this.data['historyValue'] = customerDetailInfo['historyValue'];
			this.data['compareStatus'] = customerDetailInfo['compareStatus'];
			this.data['mode'] = customerDetailInfo['mode'];
		}
		var customerService = goog.dom.getElement(idEnums.CUSTOMER_SERVICE_DETAIL);
		var customerServiceValue = customerService.value;
		var customerServiceDetail = JSON.parse(customerServiceValue);
		this.data['viewServiceId'] = customerServiceDetail['viewServiceId'];
		this.data['editServiceId'] = customerServiceDetail['editServiceId'];
		this.data['customerForecastCycleId'] = customerServiceDetail['customerForecastCycleId'];
		this.data['inhouseForecastCycleId'] = customerServiceDetail['inhouseForecastCycleId'];
		this.data['inhouseDialogStatus'] = customerServiceDetail['inhouseDialogStatus'];
		this.data['portalMethod'] = customerServiceDetail['portalMethod'];
		this.data['statusParamKey'] = customerServiceDetail['statusParamKey'];
		this.data['inhouseStatusParamKey'] = customerServiceDetail['inhouseStatusParamKey'];
		this.data['onChangeFlag'] = false;
		this.data['addRowFlag'] = false;
		this.data['compareStatusFlag'] = false;
		this.data['dragFlag'] = false;
		this.data['saveButtonStatus'] = false;
		this.data['backStatus']=customerServiceDetail['backStatus'];

		this.commentButton_ = this.getComponent_(idEnums.COMMNET_BUTTON);
		this.alertButton_ = this.getComponent_(idEnums.ALERT_BUTTON);
		this.historyButton_ = this.getComponent_(idEnums.HISTORY_BUTTON);
		this.backToPortalButton_ = this.getComponent_(idEnums.BACK_TO_PORTAL_BUTTON);
		this.gridCompareDropDownButton_ = this.getComponent_(idEnums.GRID_COMPARE_DROP_DOWN_BUTTON);
		this.rangeDropDownButton_ = this.getComponent_(idEnums.RANGE_DROPDOWN_BUTTON);
		this.inputControlDropDownButton_ = this.getComponent_(idEnums.INPUT_CONTROL_DROPDOWN_BUTTON);
		this.saveButton_ = this.getComponent_(idEnums.SAVE_BUTTON);
		this.searchButton_ = this.getComponent_(idEnums.SEARCH_BUTTON);
		this.settingButton_ = this.getComponent_(idEnums.SETTING_BUTTON);
		this.expandButton_ = this.getComponent_(idEnums.EXPAND_BUTTON);
		this.contractButton_ = this.getComponent_(idEnums.CONTRACT_BUTTON);
		this.previousButton_ = this.getComponent_(idEnums.PREVIOUS_BUTTON);
		this.nextButton_ = this.getComponent_(idEnums.NEXT_BUTTON);
		this.inquiryLinkLabel_ = this.getComponent_(idEnums.INQUIRY_LINK_LABEL);
		this.detailsGrid_ = this.getComponent_(idEnums.DETAIL_GRID);
		this.addButton_ = this.getComponent_(idEnums.ADD_BUTTON);
		this.inhouseDialog_ = this.getComponent_(idEnums.INHOUSE_DIALOG);
		this.inhouseInboxList_ = this.getComponent_(idEnums.INHOUSE_INBOX_LIST);
		this.backToViewButton_ = this.getComponent_(idEnums.BACK_TO_VIEW_BUTTON);
		this.saveConfirmationDialog_ = this.getComponent_(idEnums.SAVE_CONFIRMATION_DIALOG);
		this.scmCommonObject_ = new wap.scm.sales.customerforecastmanagement.customerforecastmanagement.common.ScmSalesCommon();
		this.backToInquiry_ = this.getComponent_(idEnums.BACK_TO_INQUIRY);
		this.componentObjects = {
				'contractButton': this.contractButton_,
				'expandButton': this.expandButton_,
				'gridComp': this.detailsGrid_
		};
		if (this.data['mode'] === constants.VIEW_MODE) {
			var contextMenuItem = this.detailsGrid_.getPlugin('wap.core.ui.sima.grid.ContextMenu');
			contextMenuItem.getPopupMenu_().removeItemAt(0);
			contextMenuItem.getPopupMenu_().removeItemAt(0);
			contextMenuItem.getPopupMenu_().removeItemAt(0);
		}

		this.detailsGrid_.appendRows({});
		this.detailsGrid_.deleteRows(this.detailsGrid_.getRowsCount() - 1);
	};
	/**
	 * eventHandlers_ handles all the event of the screen
	 * @private
	 */
	customerForecastDetail.prototype.eventHandlers_ = function() {
		var eventEnum = customerForecastDetail.EVENT_ENUM_;
		this.initialize_();
		this.listen_(this.backToPortalButton_, eventEnum.BUTTON_ACTION, this.backToPortal_);
		this.listen_(this.expandButton_, eventEnum.BUTTON_ACTION, this.expandGrid_);
		this.listen_(this.contractButton_, eventEnum.BUTTON_ACTION, this.contractGrid_);
		this.listen_(this.previousButton_, eventEnum.BUTTON_ACTION, this.navigationButtonClick_);
		this.listen_(this.saveButton_, eventEnum.BUTTON_ACTION, this.doOnSave_);
		this.listen_(this.nextButton_, eventEnum.BUTTON_ACTION, this.navigationButtonClick_);
		this.listen_(this.historyButton_, eventEnum.DROP_DOWN_BUTTON_ACTION, this.performHistoryItemActions_);
		this.listen_(this.detailsGrid_.getSheet(), eventEnum.CELL_EDIT, this.doOnChange_);
		this.listen_(this.detailsGrid_.getSheet(), eventEnum.CELL_CHANGE, this.doOnCellChange_);
		this.listen_(this.detailsGrid_.getSheet(), eventEnum.BEGIN_CELL_EDITING, this.doOnBeginCellChangeEditing_);
		this.listen_(this.detailsGrid_.getSheet(), eventEnum.DRAG_CHANGE, this.doOnDrag_);
		this.listen_(this.detailsGrid_.getSheet(), eventEnum.PASTE_CHANGE, this.doOnDrag_);
		this.listen_(this.rangeDropDownButton_, eventEnum.DROP_DOWN_BUTTON_ACTION, this.showRangeDropDownActions_);
		this.listen_(this.backToViewButton_, eventEnum.BUTTON_ACTION, this.backToView_);
		this.listen_(this.gridCompareDropDownButton_, eventEnum.DROP_DOWN_BUTTON_ACTION, this.performGridCompareActions_);
		this.listen_(this.inputControlDropDownButton_, eventEnum.DROP_DOWN_BUTTON_ACTION, this.inputControlDropDownButtonActions_);
		this.listen_(this.inhouseInboxList_, eventEnum.INBOX_CLICK_ROW, this.loadInhouseDetail_);
		this.listen_(this.addButton_, eventEnum.BUTTON_ACTION, this.appendRow_);
		this.listen_(this.detailsGrid_.getSheet(), wap.core.ui.sima.Sheet.EventType.CONTEXT_MENU, this.onContextMenuAction_);
		this.listen_(this.saveConfirmationDialog_, eventEnum.SAVE_CONFIRMATION_DIALOG_YES_ACTION, this.backToViewByConfirmation_);
		this.listen_(this.backToInquiry_, eventEnum.BUTTON_ACTION, this.loadInquiryByDetailScreen_);
	};


	/**
	 * onContextMenuAction_ methods uesd to show the context menu for view screen
	 * @param {Object} event
	 * @private
	 */

	customerForecastDetail.prototype.onContextMenuAction_ = function(event) {
		var sheet = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getSheet();
		var column = sheet.getActiveCell()['column'];
		var contextMenuItem = this.detailsGrid_.getPlugin('wap.core.ui.sima.grid.ContextMenu');
		var popMenu_ = contextMenuItem.getPopupMenu_();
		if (this.data['mode'] === customerForecastDetail.CONSTANTS_ENUM_.VIEW_MODE) {
			if (column === 1) {

				if (popMenu_.children_.length === 1) {
					popMenu_.getItemAt(0).setVisible(false);
				} else {
					contextMenuItem.getPopupMenu_().removeItemAt(0);
					popMenu_.getItemAt(0).setVisible(false);

				}
			} else {
				if (popMenu_.children_.length === 1) {
					popMenu_.getItemAt(0).setVisible(true);
				} else {
					popMenu_.getItemAt(0).setVisible(false);
				}

			}
		} else {
			this.loadContextMenuForEditDetail_();
		}
	};

	/**
	 * loadContextMenuForEditDetail_ methos used to load the contextMenu for edit screen.
	 * @private
	 */
	customerForecastDetail.prototype.loadContextMenuForEditDetail_ = function() {
		var sheet = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getSheet();
		var contextMenuItem = this.detailsGrid_.getPlugin('wap.core.ui.sima.grid.ContextMenu');
		var popMenu_ = contextMenuItem.getPopupMenu_();
		popMenu_.getItemAt(3).setVisible(false);
		popMenu_.getItemAt(4).setVisible(false);
		popMenu_.getItemAt(5).setVisible(false);
		popMenu_.getItemAt(6).setVisible(false);
		if (sheet.getActiveCell()['column'] === 1) {
			popMenu_.getItemAt(0).setVisible(false);
			popMenu_.getItemAt(1).setVisible(false);
			popMenu_.getItemAt(2).setVisible(false);
			popMenu_.getItemAt(7).setVisible(false);
			popMenu_.getItemAt(8).setVisible(false);
		} else {
			popMenu_.getItemAt(0).setVisible(true);
			popMenu_.getItemAt(1).setVisible(true);
			popMenu_.getItemAt(2).setVisible(true);
			popMenu_.getItemAt(7).setVisible(true);
		}
		if (sheet.getCellData(sheet.getActiveCell()['row'], 2) === null) {
			popMenu_.getItemAt(7).setVisible(false);
		} else {
			popMenu_.getItemAt(7).setVisible(true);
		}

	};




	/**
	 * loadInquiryScreen_ navigates the customer inquiry screen
	 * @private
	 */
	customerForecastDetail.prototype.loadInquiryScreen_ = function() {
		var idEnums = customerForecastDetail.ID_ENUMS_;
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var serviceLinkId;
		if (this.data['mode'] === constants.VIEW_MODE) {
			serviceLinkId = 'CustomerForecastDetailViewLinkInquiry';
		} else {
			serviceLinkId = 'CustomerForecastDetailEditLinkInquiry';
		}
		var customerId = goog.dom.getElement(idEnums.CUSTOMER_ID);
		var url = '/hue/scm/sales/customerforecastmanagement/customerforecastmanagement/customerforecastdetail/' +
		'loadCustomerInquiryInboxList?customerId=' + customerId.value;
		this.servicelink_.openLinkInNewTab(url, serviceLinkId);
	};


	/**
	 * loadInquiryScreen_ navigates the customer inquiry screen
	 * @private
	 */
	customerForecastDetail.prototype.loadInquiryByDetailScreen_ = function() {
		var idEnums = customerForecastDetail.ID_ENUMS_;
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var serviceLinkId;
		if (this.data['mode'] === constants.VIEW_MODE) {
			serviceLinkId = 'CustomerForecastDetailViewLinkInquiry';
		} else {
			serviceLinkId = 'CustomerForecastDetailEditLinkInquiry';
		}
		var customerId = goog.dom.getElement(idEnums.CUSTOMER_ID);
		var url = '/hue/scm/sales/customerforecastmanagement/customerforecastmanagement/customerforecastdetail/' +
		'loadCustomerInquiryInboxList?customerId=' + customerId.value;
		this.servicelink_.openLink(url, serviceLinkId);
	};


	/**
	 * loadInhouseDetail_ navigates the customer inquiry screen
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.loadInhouseDetail_ = function(event) {
		var inhouseForecastId = event['target'].getKey();
		var url = '/hue/scm/sales/customerforecastmanagement/customerforecastmanagement/inhouseforecastdetail/' +
		'index?inhouseForecastCycleId=' + inhouseForecastId+'&customerStatus='+this.data['backStatus']+'&status=backToCustomer';
		var serviceLinkId = 'CustomerToInquiryDetailLink';
		this.servicelink_.openLink(url, serviceLinkId);
		this.inhouseDialog_.close();
	};

	/**
	 * appendRow_ append the row into the grid
	 * @private
	 */
	customerForecastDetail.prototype.appendRow_ = function() {
		var detailsGrid = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID);
		detailsGrid.appendRows([{}]);
		this.data['addRowFlag'] = true;
		this.data['onChangeFlag'] = true;
		this.data['gridDataList'] = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getRows();
		this.initialOnChange_();
	};


	/**
	 * backToPortal_ navigates the portal page .
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.backToPortal_ = function(event) {
		var constants=customerForecastDetail.CONSTANTS_ENUM_;
		var url = constants.PORTAL_URL + constants.INDEX_APPEND;
		var serviceLinkId = 'CustomerToPortalLink';
		this.servicelink_.openLink(url, serviceLinkId);
	};

	/**
	 * backToView_ navigates the portal page .
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.backToView_ = function(event) {
		if (this.saveButton_.isEnabled()) {
			this.saveConfirmationDialog_.show();
		} else {
			this.backToViewBlock_();
		}
	};

	/**
	 * backToViewBlock_ navigates the view screen.
	 * @private
	 */
	customerForecastDetail.prototype.backToViewBlock_ = function() {
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var url = constants.CONTROLLER_URL + 'index?customerForecastCycleId=' + this.data['customerForecastCycleId']+'&status='+this.data['backStatus'];
		var serviceLinkId = 'CustomerForecastDetailEditLinkDetailView';
		this.servicelink_.openLink(url, serviceLinkId);
	};

	/**
	 * backToViewByConfirmation_ navigates the portal page .
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.backToViewByConfirmation_ = function(event) {
		this.backToViewBlock_();
	};

	/**
	 * getText_ is used to fetch text value.
	 * @private
	 * @param {Object} textId
	 * @param {Object} params
	 * @return {Object} textResource
	 */
	customerForecastDetail.prototype.getText_ = function(textId, params) {
		return wap.core.txt.TextResource.getText(textId, params);
	};


	/**
	 * doOnSave_ navigates the portal page .
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.doOnSave_ = function(event) {
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var gridValue = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getRows();
		customerForecastDetailDao.onSaveClick_(gridValue).getResult().then(goog.bind(function(response) {
			var status = JSON.parse(response.getResponseText());
			if (!status) {
				this.getText_(constants.SAVED_SUCESSFULLY).then(
						function(text) {
							this.getComponent_(customerForecastDetail.ID_ENUMS_.WAP_TOASTR).success(text);
						}.bind(this));
				this.onViewUrl_();
			} else {
				this.getText_(constants.SAVED_UNSUCESSFULLY).then(
						function(text) {
							this.getComponent_(customerForecastDetail.ID_ENUMS_.WAP_TOASTR).success(text);
						}.bind(this));

			}
			this.saveButton_.disable();
		}, this));
	};




	/**
	 * onViewUrl_ open the view screen url .
	 * @private
	 */
	customerForecastDetail.prototype.onViewUrl_ = function() {
		this.changeMode_(customerForecastDetail.CONSTANTS_ENUM_.VIEW_MODE);
	};

	/**
	 * onEditUrl_ open the edit screen url .
	 * @private
	 */
	customerForecastDetail.prototype.onEditUrl_ = function() {
		this.changeMode_(customerForecastDetail.CONSTANTS_ENUM_.EDIT);
	};


	/**
	 * changeMode_ methods for mode change .
	 * @param {Object} mode
	 * @private
	 */
	customerForecastDetail.prototype.changeMode_ = function(mode) {
		var paramMap = {};
		var serviceLinkId;
		this.windowObject_ = goog.dom.getWindow();
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		if (mode === constants.VIEW_MODE) {
			paramMap['historyValue'] = constants.EMPTY;
			serviceLinkId = 'CustomerForecastDetailEditLinkDetailView';
			this.data['mode'] = constants.VIEW_MODE;
		} else {
			paramMap['historyValue'] = this.data['historyValue'];
			serviceLinkId = 'CustomerForecastDetailViewLinkDetailEdit';
			this.data['mode'] = constants.EDIT;
		}

		paramMap['customerForecastCycleId'] = this.data['customerForecastCycleId'];
		paramMap['status'] = this.data['compareStatus'];
		paramMap['mode'] = mode;
		paramMap['method'] = constants.MODE_CHANGE_METHOD;

		customerForecastDetailDao.changeMode_(paramMap).getResult().then(goog.bind(function(response) {
			var url = constants.CONTROLLER_URL + 'changeModeIndex?customerForecastCycleId=' + 
			this.data['customerForecastCycleId']+'&status='+this.data['backStatus'];
			this.servicelink_.openLink(url, serviceLinkId);
		}, this));
	};

	/**
	 * performGridCompareActions_ performs according to the selected action.
	 *
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.performGridCompareActions_ = function(event) {
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		if (this.data['addRowFlag'] && this.data['compareStatus'] !== event['data']['value']) {
			this.data['compareStatusFlag'] = true;
		} else {
			this.data['compareStatusFlag'] = false;
		}
		this.data['compareStatus'] = event['data']['value'];
		var paramMap = {};
		if (this.data['historyValue'] !== constants.EMPTY) {
			paramMap['historyValue'] = this.data['historyValue'];
		} else {
			paramMap['historyValue'] = constants.EMPTY;
		}

		this.gridCompareDropDownButton_.setLabel(event['data']['label']);
		paramMap['compareStatus'] = this.data['compareStatus'];
		paramMap['method'] = constants.COMPARE_DROP_DOWN_CLICK;
		paramMap['onChangeFlag'] = this.data['onChangeFlag'];
		paramMap['gridDataList'] = this.data['gridDataList'];
		paramMap['addRowFlag'] = this.data['addRowFlag'];
		paramMap['compareStatusFlag'] = this.data['compareStatusFlag'];
		customerForecastDetailPage.dropDownClick_(paramMap).
		then(goog.bind(this.navigationButtonClickThen_, this));
		if (this.data['compareStatusFlag']) {
			this.data['compareStatusFlag'] = false;
		}

	};

	/**
	 * inputControlDropDownButtonActions_ performs according to the selected action.
	 *
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.inputControlDropDownButtonActions_ = function(event) {
		var idEnums = customerForecastDetail.ID_ENUMS_;
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var paramMap = {};
		paramMap.method = 'setPortalStatus';
		var gridCompareDropDownButtonActionId = event['data']['value'];
		if (gridCompareDropDownButtonActionId === constants.EDIT_NOTICE_DROP_DOWN_ITEM) {
			this.backToPortalButton_.setDisplayed(false);
			this.getComponent_(idEnums.ADD_BUTTON).setDisplayed(true);
			this.saveButton_.setDisplayed(true);
			this.data['mode'] = customerForecastDetail.CONSTANTS_ENUM_.EDIT;
			this.onEditUrl_();
		} else if (gridCompareDropDownButtonActionId === constants.VIEW_NOTICE_DROP_DOWN_ITEM) {
			if (this.data['inhouseDialogStatus']) {
				this.getText_(constants.INHOUSE_TOASTR_MESSAGE).then(
						function(text) {
							this.getComponent_(customerForecastDetail.ID_ENUMS_.WAP_TOASTR).success(text);
						}.bind(this));
			} else {
				customerForecastDetailPage.dropDownClick_(paramMap).
				then(goog.bind(this.navigationButtonClickThen_, this));
				this.inhouseDialog_.show();
			}

		}
	};


	/**
	 * doOnChange_ works when changes in the Grid cell .
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.doOnChange_ = function(event) {
		this.saveButton_.enable();
		this.data['saveButtonStatus'] = true;
		this.data['onChangeFlag'] = true;
		this.data['gridDataList'] = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getRows();
	};


	/**
	 * doOnDrag_ works when changes in the Grid cell .
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.doOnDrag_ = function(event) {
		this.doOnChange_();
		this.data['onDragFlag'] = true;
	};

	/**
	 * doOnBeginCellChangeEditing_ works when changes in the Grid cell .
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.doOnBeginCellChangeEditing_ = function(event) {
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var gridSheet = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getSheet();
		var rowIndex = event['data']['row'];
		var columnIndex = event['data']['column'];
		if (event['data']['column'] === constants.SECOND_COLUMN) {
			this.data['beginCellValue'] = constants.ASSIGN_ZERO_VALUE;
		} else {
			this.data['beginCellValue'] = gridSheet.getCellData(rowIndex, columnIndex);
		}
	};


	/**
	 * doOnCellChange_ navigates the portal page .
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.doOnCellChange_ = function(event) {
		var paramMap = {};
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var rowIndex = event['data']['cells']['0']['row'];
		var columnIndex = event['data']['cells']['0']['column'];
		var gridSheet = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getSheet();
		var detailGrid = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID);
		var gridIndex = detailGrid.getActiveRowIndex();
		var cellValue = gridSheet.getCellData(rowIndex, columnIndex);
		var columnId = detailGrid.getColumnAt(constants.SUM_INDEX);
		var gridList = detailGrid.getItems();
		var columnName = columnId.getId();


		if (cellValue === constants.EMPTY || columnIndex === constants.SECOND_COLUMN || cellValue === null) {
			cellValue = constants.COMPARE_ZERO_INDEX;
		}
		if (this.data['beginCellValue'] === undefined) {
			// no opertaion
		} else if (columnIndex !== constants.SECOND_COLUMN && cellValue !== this.data['beginCellValue']) {
			this.doOnChange_();
		} else if (columnIndex === constants.SECOND_COLUMN) {
			var gridValue = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getRows();
			var paramDataMap = {};
			paramDataMap['gridDataList'] = gridValue;
			paramDataMap['label'] = gridSheet.getCellData(rowIndex, columnIndex);
			customerForecastDetailDao.onDuplicateCheck_(paramDataMap).getResult().then(goog.bind(function(response) {
				var status = JSON.parse(response.getResponseText());
				if (status) {

					this.getText_(constants.ITEM_DUPLICATE_MESSAGE).then(
							function(text) {
								this.getComponent_(customerForecastDetail.ID_ENUMS_.WAP_TOASTR).success(text);
							}.bind(this));
					detailGrid.updateCellAt(gridIndex, 'item', null, null, false);
					detailGrid.updateCellAt(gridIndex, 'itemName', null, null, false);
				} else {
					gridList[gridIndex]['id'] = gridSheet.getCellData(rowIndex, columnIndex);
					detailGrid.setItems(gridList, null, false);
					detailGrid.updateCellAt(gridIndex, 'item', gridSheet.getCellData(rowIndex, columnIndex), null,
							false);
					this.doOnChange_();
				}
			}, this));
		}
		if (this.data['onDragFlag'] && columnName.indexOf(constants.SUM) !== -1) {
			if (this.data['historyValue'] !== constants.EMPTY) {
				paramMap['historyValue'] = this.data['historyValue'];
			} else {
				paramMap['historyValue'] = constants.EMPTY;
			}
			this.data['onChangeFlag'] = true;
			this.data['gridDataList'] = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getRows();
			this.data['onDragFlag'] = false;
			paramMap['compareStatus'] = this.data['compareStatus'];
			paramMap['historyValue'] = this.data['historyValue'];
			paramMap['method'] = constants.COMPARE_DROP_DOWN_CLICK;
			paramMap['onChangeFlag'] = this.data['onChangeFlag'];
			paramMap['gridDataList'] = this.data['gridDataList'];
			paramMap['addRowFlag'] = this.data['addRowFlag'];
			paramMap['compareStatusFlag'] = this.data['compareStatusFlag'];

			customerForecastDetailPage.dropDownClick_(paramMap).
			then(goog.bind(this.navigationButtonClickThen_, this));
		} else if (columnName.indexOf(constants.SUM) !== -1) {
			var total = gridSheet.getCellData(rowIndex, constants.SUM_INDEX);
			gridList[gridIndex][columnName] = total - this.data['beginCellValue'] + parseInt(cellValue);
			detailGrid.setItems(gridList);
		}

	};

	/**
	 * 
	 * performHistoryItemActions_ navigates the page to list page.
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.performHistoryItemActions_ = function(event) {
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var historyData = event['data']['label'];
		this.data['onChangeFlag'] = false;
		var paramMap = {};
		this.historyButton_.setLabel(historyData);
		if (event['data']['value'] === constants.CURRENT_DATE) {
			this.data['historyValue'] = constants.EMPTY;
		} else {
			this.data['historyValue'] = event['data']['value'];
		}
		paramMap['compareStatus'] = this.data['compareStatus'];
		paramMap['historyValue'] = this.data['historyValue'];
		paramMap['method'] = constants.COMPARE_DROP_DOWN_CLICK;
		paramMap['onChangeFlag'] = this.data['onChangeFlag'];
		paramMap['addRowFlag'] = this.data['addRowFlag'];
		paramMap['compareStatusFlag'] = this.data['compareStatusFlag'];
		customerForecastDetailPage.dropDownClick_(paramMap).
		then(goog.bind(this.navigationButtonClickThen_, this));


	};


	/**
	 * navigationButtonClick_ navigates the grid.
	 * 
	 * @param {Object} event
	 * @private
	 */

	customerForecastDetail.prototype.navigationButtonClick_ = function(event) {
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var buttonId = event['data']['id'];
		this.data['gridDataList'] = this.getComponent_(customerForecastDetail.ID_ENUMS_.DETAIL_GRID).getRows();
		var paramMap = {};
		var status;
		var idEnums = customerForecastDetail.ID_ENUMS_;
		if (buttonId === idEnums.NEXT_BUTTON) {
			status = constants.NEXT;
		} else {
			status = constants.PREVIOUS;
		}
		if (this.data['historyValue'] !== constants.EMPTY) {
			paramMap['historyValue'] = this.data['historyValue'];
		} else {
			paramMap['historyValue'] = constants.EMPTY;
		}
		if (this.data['onChangeFlag']) {
			paramMap['onChangeFlag'] = this.data['onChangeFlag'];
			paramMap['gridDataList'] = this.data['gridDataList'];
		} else {
			paramMap['onChangeFlag'] = false;
		}
		paramMap['status'] = status;
		paramMap['method'] = constants.NAVIGATION_BUTTON_CLICK;
		paramMap['compareStatus'] = this.data['compareStatus'];
		paramMap['addRowFlag'] = this.data['addRowFlag'];
		paramMap['compareStatusFlag'] = this.data['compareStatusFlag'];
		customerForecastDetailPage.dropDownClick_(paramMap).
		then(goog.bind(this.navigationButtonClickThen_, this));
	};

	/**
	 * navigationButtonClickThen_ method bind the components.
	 * 
	 * @private
	 */
	customerForecastDetail.prototype.navigationButtonClickThen_ = function() {
		this.inputFormModelHelper_ = new wap.ri.core.InputFormModelHelper(this);
		var idEnums = customerForecastDetail.ID_ENUMS_;
		var eventEnum = customerForecastDetail.EVENT_ENUM_;
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		this.detailsGrid_ = this.getComponent_(idEnums.DETAIL_GRID);
		if (this.data['mode'] === constants.VIEW_MODE) {
			var contextMenuItem = this.detailsGrid_.getPlugin('wap.core.ui.sima.grid.ContextMenu');
			contextMenuItem.getPopupMenu_().removeItemAt(0);
			contextMenuItem.getPopupMenu_().removeItemAt(0);
			contextMenuItem.getPopupMenu_().removeItemAt(0);
		}
		if (this.data['saveButtonStatus']) {
			this.saveButton_.enable();
		}
		this.detailsGrid_.appendRows({});
		this.detailsGrid_.deleteRows(this.detailsGrid_.getRowsCount() - 1);
		this.listen_(this.detailsGrid_.getSheet(), wap.core.ui.sima.Sheet.EventType.CONTEXT_MENU, this.onContextMenuActionThen_);
		this.listen_(this.getComponent_(idEnums.DETAIL_GRID).getSheet(), eventEnum.CELL_EDIT, this.doOnChange_);
		this.listen_(this.getComponent_(idEnums.DETAIL_GRID).getSheet(), eventEnum.CELL_CHANGE, this.doOnCellChange_);
		this.listen_(this.getComponent_(idEnums.DETAIL_GRID).getSheet(), eventEnum.BEGIN_CELL_EDITING, this.doOnBeginCellChangeEditing_);
		this.listen_(this.getComponent_(idEnums.DETAIL_GRID).getSheet(), eventEnum.DRAG_CHANGE, this.doOnDrag_);
		this.listen_(this.getComponent_(idEnums.DETAIL_GRID).getSheet(), eventEnum.PASTE_CHANGE, this.doOnDrag_);
		this.listen_(this.getComponent_(idEnums.RANGE_DROPDOWN_BUTTON), eventEnum.DROP_DOWN_BUTTON_ACTION, this.showRangeDropDownActions_);
		this.listen_(this.getComponent_(idEnums.NEXT_BUTTON), eventEnum.BUTTON_ACTION, this.navigationButtonClick_);
		this.listen_(this.getComponent_(idEnums.PREVIOUS_BUTTON), eventEnum.BUTTON_ACTION, this.navigationButtonClick_);
		this.listen_(this.getComponent_(idEnums.HISTORY_BUTTON), eventEnum.DROP_DOWN_BUTTON_ACTION, this.performHistoryItemActions_);
		this.listen_(this.getComponent_(idEnums.ADD_BUTTON), eventEnum.BUTTON_ACTION, this.appendRow_);
		if(this.data['expandGrid']){
			var grid=this.detailsGrid_;
			var sheet=this.detailsGrid_.getSheet();
			grid.doSize();
			grid.setOuterSize('100%', 'auto');
			sheet.setOuterSize('100%', 'auto');
		}
	};

	/**
	 * onContextMenuActionThen_ methods uesd to show the context menu for view screen
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.onContextMenuActionThen_ = function(event) {
		var sheet = this.detailsGrid_.getSheet();
		var column = sheet.getActiveCell()['column'];
		var contextMenuItem = this.detailsGrid_.getPlugin('wap.core.ui.sima.grid.ContextMenu');
		var popMenu_ = contextMenuItem.getPopupMenu_();
		if (this.data['mode'] === customerForecastDetail.CONSTANTS_ENUM_.VIEW_MODE) {
			if (column === 1) {

				if (popMenu_.children_.length === 1) {
					popMenu_.getItemAt(0).setVisible(false);
				} else {
					contextMenuItem.getPopupMenu_().removeItemAt(0);
					contextMenuItem.getPopupMenu_().removeItemAt(0);
					contextMenuItem.getPopupMenu_().removeItemAt(0);
					contextMenuItem.getPopupMenu_().removeItemAt(0);
					popMenu_.getItemAt(0).setVisible(false);

				}
			} else {
				if (popMenu_.children_.length === 1) {
					popMenu_.getItemAt(0).setVisible(true);
				} else {
					popMenu_.getItemAt(0).setVisible(false);
				}

			}
		} else {
			this.loadContextMenuForEditDetail_();
		}
	};


	/**
	 * showRangeDropDownActions_ performs the operations of range drop down button.
	 * @param {Object} event
	 * @private
	 */
	customerForecastDetail.prototype.showRangeDropDownActions_ = function(event) {
		var constants = customerForecastDetail.CONSTANTS_ENUM_;
		var showRangeDropDownActionIndex = event['data']['value'];
		var setLabelContent = event['data']['label'];
		this.rangeDropDownButton_.setLabel(setLabelContent);
		var paramMap = {};

		if (this.data['historyValue'] !== constants.EMPTY) {
			paramMap['historyValue'] = this.data['historyValue'];
		} else {
			paramMap['historyValue'] = constants.EMPTY;
		}
		if (this.data['onChangeFlag']) {
			paramMap['onChangeFlag'] = this.data['onChangeFlag'];
			paramMap['gridDataList'] = this.data['gridDataList'];
		} else {
			paramMap['onChangeFlag'] = false;
		}
		paramMap['status'] = showRangeDropDownActionIndex;
		paramMap['method'] = constants.NAVIGATION_BUTTON_CLICK;
		paramMap['compareStatus'] = this.data['compareStatus'];
		paramMap['addRowFlag'] = this.data['addRowFlag'];
		paramMap['compareStatusFlag'] = this.data['compareStatusFlag'];
		customerForecastDetailPage.dropDownClick_(paramMap).
		then(goog.bind(this.navigationButtonClickThen_, this));
	};
	/**
	 * initialOnChange_ methods to add and delete the rows.
	 * @private
	 */
	customerForecastDetail.prototype.initialOnChange_ = function() {
		var idEnums = customerForecastDetail.ID_ENUMS_;
		this.detailsGrid_ = this.getComponent_(idEnums.DETAIL_GRID);
		this.detailsGrid_.appendRows({});
		this.detailsGrid_.deleteRows(this.detailsGrid_.getRowsCount() - 1);
	};

	/**
	 * expandGrid_ expands the Grid into full screen view.
	 * @private
	 */
	customerForecastDetail.prototype.expandGrid_ = function() {
		var idEnums = customerForecastDetail.ID_ENUMS_;
		var targetIds = [idEnums.BUTTON_DIV, idEnums.GRID_ROW];
		this.initialOnChange_();
		this.data['expandGrid']=true;
		this.componentObjects['gridComp'] = this.detailsGrid_;
		this.scmCommonObject_.expandGrid(targetIds, this.componentObjects, customerForecastDetail.CONSTANTS_ENUM_.DETAIL_ROOM);
		this.initialOnChange_();
	};

	/**
	 * contractGrid_ contracts the Grid into normal size.
	 * 
	 * @private
	 */
	customerForecastDetail.prototype.contractGrid_ = function() {
		this.initialOnChange_();
		this.componentObjects['gridComp'] = this.detailsGrid_;
		this.data['expandGrid']=false;
		this.scmCommonObject_.contractGrid(this.componentObjects, customerForecastDetail.CONSTANTS_ENUM_.DETAIL_ROOM);
		goog.dom.getElement(customerForecastDetail.ID_ENUMS_.EXPAND_BUTTON).scrollIntoView();
		this.initialOnChange_();
	};
});