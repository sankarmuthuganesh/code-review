goog.provide('wap.scm.procurement.purchaseorder.orderapplication.PurchaseOrderApplicationInput');

goog.require('goog.Uri');
goog.require('goog.array');
goog.require('goog.date.Date');
goog.require('goog.dom');
goog.require('goog.events.EventType');
goog.require('goog.locale');
goog.require('goog.math');
goog.require('goog.string');
goog.require('goog.string.StringBuffer');
goog.require('goog.window');
goog.require('wap.core.app.View');
goog.require('wap.core.common');
goog.require('wap.core.log.LoggerFactory');
goog.require('wap.core.txt.MLStringUtils');
goog.require('wap.core.txt.TextResource');
goog.require('wap.core.ui.WapAnchorLabel.EventType');
goog.require('wap.core.ui.WapButton.EventType');
goog.require('wap.core.ui.WapDateInput.EventType');
goog.require('wap.core.ui.WapMasterInput.EventType');
goog.require('wap.core.ui.WapNumericInput.EventType');
goog.require('wap.core.ui.WapSingleSelect.EventType');
goog.require('wap.core.ui.WapSplitButton.EventType');
goog.require('wap.core.ui.WapTagLabel.EventType');
goog.require('wap.core.ui.WapTextInput.EventType');
goog.require('wap.core.ui.container.FlexibleInputBusinessArea');
goog.require('wap.core.ui.hueDrive.FileSelector.EventType');
goog.require('wap.core.ui.hueDrive.FileUpload.EventType');
goog.require('wap.core.ui.sima.Sheet.EventType');
goog.require('wap.core.ui.sima.editor.SingleSelectEditor');
goog.require('wap.core.util.number');
goog.require('wap.core.util.style');
goog.require('wap.ivtl.ui.IvtlFileAttachmentList.EventType');
goog.require('wap.scm.procurement.common.ScmCommon');
goog.require('wap.scm.procurement.purchaseorder.orderapplication.dao.PurchaseOrderApplicationInputDao');




/**
 * wap.scm.procurement.purchaseorder.orderapplication.PurchaseOrderApplicationInput handles the client
 * side action for the Input Page
 * @author Naveenkumar S
 * @class {wap.scm.procurement.purchaseorder.orderapplication.PurchaseOrderApplicationInput}
 * @constructor
 * @extends {wap.core.ui.container.FlexibleInputBusinessArea}
 *
 *
 */
wap.scm.procurement.purchaseorder.orderapplication.PurchaseOrderApplicationInput = function() {
  this.logger_ = wap.core.log.LoggerFactory
    .getLogger('wap.scm.procurement.purchaseorder.orderapplication.PurchaseOrderApplicationInput');
  goog.base(this);
};
goog.inherits(
  wap.scm.procurement.purchaseorder.orderapplication.PurchaseOrderApplicationInput,
  wap.core.ui.container.FlexibleInputBusinessArea);

wap.core.app.View
  .registerComponent(
    'wap.scm.procurement.purchaseorder.orderapplication.PurchaseOrderApplicationInput',
    wap.scm.procurement.purchaseorder.orderapplication.PurchaseOrderApplicationInput);

goog.scope(function() {
  var purchaseOrderApplicationInput = wap.scm.procurement.purchaseorder.orderapplication.PurchaseOrderApplicationInput;
  var purchaseOrderApplicationInputDao = wap.scm.procurement.purchaseorder.orderapplication.dao.PurchaseOrderApplicationInputDao;
  /**
   * idEnum_ is an Enum of IDs of components.
   *
   * @private
   * @enum {string}
   */
  purchaseOrderApplicationInput.ID_ENUM_ = {

    RETURN_BUTTON: 'return-button',
    ORDER_APPLICATION_LINE_DETAILS_SIMA_GRID: 'order-application-line-details-sima-grid',
    ESTIMATE_NOT_SELECT_DETAILS_SIMA_GRID: 'estimate-not-select-details-sima-grid',
    LINE_CONTRACT_BUTTON: 'line-contract-button',
    LINE_EXPAND_BUTTON: 'line-expand-button',
    TOTAL_SALE_AMOUNT_MONEY_INPUT: 'total-sale-amount-money-input',
    CONSUMPTION_TAX_AMOUNT_MONEY_INPUT: 'consumption-tax-amount-money-input',
    TAX_INCLUDED_AMOUNT_MONEY_INPUT: 'tax-included-amount-money-input',
    TAX_WITHDRAWAL_AMOUNT_MONEY_INPUT: 'tax-withdrawal-amount-money-input',
    ENTER_DETAIL_INFORMATION_PAYMENT_ANCHOR_LABEL: 'enter-detail-information-payment-anchor-label',
    UPLOADER_FROM_PC_HUE_DRIVE_FILE_UPLOAD: 'uploader-from-pc-hue-drive-file-upload',
    MAIL_ATTACHMENT_HUE_DRIVE_FILE_SELECTOR: 'mail-attachment-hue-drive-file-selector',
    SHARED_ATTACHMENT_HUE_DRIVE_FILE_SELECTOR: 'shared-attachment-hue-drive-file-selector',
    CONSTRUCTION_ATTACHMENT_SPLIT_BUTTON: 'construction-attachment-split-button',
    QUOTATION_OVERVIEW_SPLIT_BUTTON: 'quotation-overview-split-button',
    PREPAYMENT_TERMS_ROW: 'prepayment-terms-row',
    PREPAID_EXPENSES_TYPE_ROW: 'prepaid-expenses-type-row',
    NAME_SIMA_GRID_COLUMN: 'name-sima-grid-column',
    CONSUMPTION_TAX_SIMA_GRID_COLUMN: 'consumption-tax-sima-grid-column',
    QUANTITY_SIMA_GRID_COLUMN: 'quantity-sima-grid-column',
    TAX_EXCLUDED_AMOUNT_SIMA_GRID_COLUMN: 'tax-excluded-amount-sima-grid-column',
    TAX_INCLUDED_AMOUNT_SIMA_GRID_COLUMN: 'tax-included-amount-sima-grid-column',
    UNIT_PRICE_SIMA_GRID_COLUMN: 'unit-price-sima-grid-column',
    SALES_TAX_RATE_SIMA_GRID_COLUMN: 'sales-tax-rate-sima-grid-column',
    UNIT_SIMA_GRID_COLUMN: 'unit-sima-grid-column',
    SPECIFICATION_SUPPLEMENT_SIMA_GRID_COLUMN: 'specification-supplement-sima-grid-column',
    ITEM_NAME_SUPPLEMENT_SIMA_GRID_COLUMN: 'item-name-supplement-sima-grid-column',
    SPECIFICATION_SIMA_GRID_COLUMN: 'specification-sima-grid-column',
    MESSAGE_ROW: 'message-row',
    LINE_ITEM_GRID_ROW: 'line-item-grid-row',
    ACTUAL_CLASSIFICATION_SIMA_GRID_COLUMN: 'actual-classification-sima-grid-column',
    POSSIBILITY_OF_REPLACEMENT_SIMA_GRID_COLUMN: 'possibility-of-replacement-sima-grid-column',
    SCHEDULED_DELIVERY_SIMA_GRID_COLUMN: 'scheduled-delivery-sima-grid-column',
    SUBCONTRACT_COVERAGE_SIMA_GRID_COLUMN: 'subcontract-coverage-sima-grid-column',
    TAX_CLASSIFICATION_SIMA_GRID_COLUMN: 'tax-classification-sima-grid-column',
    CONSUMPTION_TAX_DISTINCTION_SIMA_GRID_COLUMN: 'consumption-tax-distinction-sima-grid-column',
    LINE_ERASE_BUTTON: 'line-erase-button',
    DESIRED_DELIVERY_DATE_INPUT: 'desired-delivery-date-input',
    DESIRED_DELIVERY_START_DATE_INPUT: 'desired-delivery-start-date-date-input',
    ORDER_DESIRED_DATE_INPUT: 'order-desired-date-date-input',
    ORDER_DATE_INPUT: 'order-date-date-input',
    CURRENCY_NAME_LABEL: 'currency-name-label',
    CURRENCY_SIGN_LABEL: 'currency-sign-label',
    PROJECT_MASTER_INPUT: 'project-master-input',
    PREPAYMENT_SUBJECT_MASTER_INPUT: 'prepayment-subject-master-input',
    INSTALLATION_LOCATION_MASTER_INPUT: 'installation-location-master-input',
    SENDING_METHOD_MASTER_INPUT: 'sending-method-master-input',
    DELIVERY_DESTINATION_MASTER_INPUT: 'delivery-destination-master-input',
    ACCEPTING_DEPARTMENT_MASTER_INPUT: 'accepting-department-master-input',
    RECEIVING_PERSON_MASTER_INPUT: 'receiving-person-master-input',
    PACKING_MASTER_INPUT: 'packing-master-input',
    DELIVERY_CONDITION_MASTER_INPUT: 'delivery-condition-master-input',
    ORDER_RESPONSIBLE_DEPARTMENT_MASTER_INPUT: 'order-responsible-department-master-input',
    TRANSFER_SOURCE_RESPONSIBLE_DEPARTMENT_MASTER_INPUT: 'transfer-source-responsible-department-master-input',
    TRANSFERRING_PERSON_IN_CHARGE_MASTER_INPUT: 'transferring-person-in-charge-master-input',
    VENDOR_MASTER_INPUT: 'vendor-master-input',
    PAYMENT_TERMS_MASTER_INPUT: 'payment-terms-master-input',
    PREPAID_EXPENSES_TYPE_MASTER_INPUT: 'prepaid-expenses-type-master-input',
    INSPECTION_DEPARTMENT_MASTER_INPUT: 'inspection-department-master-input',
    ACCEPTANCE_DEPARTMENT_MASTER_INPUT: 'acceptance-department-master-input',
    BURDEN_DEPARTMENT_MASTER_INPUT: 'burden-department-master-input',
    BUDGET_INFORMATION_MASTER_INPUT: 'budget-information-master-input',
    COST_CLASSIFICATION_MASTER_INPUT: 'cost-classification-master-input',
    ASSET_CLASSIFICATION_MASTER_INPUT: 'asset-classification-master-input',
    PREPAYMENT_AMOUNT_MONEY_INPUT: 'prepayment-amount-money-input',
    DESIRED_DELIVERY_START_DATE_SIMA_GRID_COLUMN: 'desired-delivery-start-date-sima-grid-column',
    DESIRED_DELIVERY_DATE_TIME_SIMA_GRID_COLUMN: 'desired-delivery-date-time-sima-grid-column',
    QUOTATION_REPLY_AMOUNT_MONEY_INPUT: 'quotation-reply-amount-money-input',
    QUOTATION_REPLY_AMOUNT_SIMA_GRID_COLUMN: 'quotation-reply-amount-sima-grid-column',
    FILE_TAGS_INPUT: 'file-tags-input',
    FILE_TAGS_INPUT_TARGET: 'file-tags-input_target',
    INHOUSE_TAGS_INPUT: 'inhouse-tags-input',
    INHOUSE_TAGS_INPUT_TARGET: 'inhouse-tags-input_target',
    APPLICATION_ID: 'application-label',
    ORDER_SUBJECT_TEXT_INPUT: 'order-subject-text-input',
    CHARGE_ORDER_TEXT_INPUT: 'representative-charge-ordering-text-input',
    PURCHASE_ORDER_TEXT_INPUT: 'purchaser-phone-number-text-input',
    PURCHASE_FAXNUMBER_TEXT_INPUT: 'purchaser-fax-number-text-input',
    PURCHASE_TEXT_INPUT: 'purchaser-text-input',
    DELIVER_TEXT_INPUT: 'deliverable-postal-code-text-input',
    ADDRESS_TEXT_INPUT: 'delivery-address-text-input',
    ORDER_REMARKS_TEXT_INPUT: 'ordering-remarks-text-input',
    CASE_TEXT_INPUT: 'case-classification-text-input',
    SERVICE_TEXT_INPUT: 'service-text-input',
    TELEPHONE_TEXT_INPUT: 'receiving-telephone-number-text-input',
    FAX_TEXT_INPUT: 'receiving-person-fax-number-text-input',
    NUMBER_TEXT_INPUT: 'purchase-order-number-text-input',
    INHOUSE_TEXT_INPUT: 'inhouse-tags-input.textInput',
    MAIL_TEXT_INPUT: 'mail-attachment-hue-drive-file-selector-search-box',
    SHARED_TEXT_INPUT: 'shared-attachment-hue-drive-file-selector-search-box',
    REQUEST_NUMBER_TEXT_INPUT: 'procurement-request-number-text-input',
    PURPOSE_TEXT_INPUT: 'purpose-of-purchase-text-input',
    CLIENT_TEXT_INPUT: 'procurement-client-business-office-text-input',
    REQUEST_TEXT_INPUT: 'procurement-request-source-affiliation-text-input',
    RECURITMENT_TEXT_INPUT: 'recruitment-of-applicants-text-input',
    REMARKS_TEXT_INPUT: 'internal-remarks-text-input',
    FILE_TEXT_INPUT: 'file-tags-input.textInput',
    ORDER_TEXT_INPUT: 'order-from-orderer-text-input',

    ORDERING_SINGLE_SELECT: 'ordering-classification-single-select',
    SUBCONTRACT_SINGLE_SELECT: 'subcontract-coverage-single-select',
    SHIPPING_SINGLE_SELECT: 'shipping-charge-type-single-select',
    SCHEDULED_SINGLE_SELECT: 'scheduled-delivery-single-select',
    POSSIBILITY_SINGLE_SELECT: 'possibility-of-replacement-single-select',
    REQUIRED_SINGLE_SELECT: 'required-for-inspection-single-select',
    CONTRACT_SINGLE_SELECT: 'contract-classificstion-single-select',

    PREPAYMENT_MONEY_INPUT: 'prepayment-amount-money-input',
    CONSUMPTION_TAX_MONEY_INPUT: 'consumption-tax-amount-money-input',
    TAX_INCLUDED_MONEY_INPUT: 'tax-included-amount-money-input',
    TOTAL_SALE_MONEY_INPUT: 'total-sale-amount-money-input',

    ORDER_NUMERIC_INPUT: 'purchase-order-version-number-numeric-input',
    MONTH_NUMERIC_INPUT: 'period-number-of-months-numeric-input',
    PAYMENT_NUMERIC_INPUT: 'total-payment-frequency-numeric-input',
    FIRST_PAYMENT_NUMERIC_INPUT: 'first-payment-tax-included-numeric-input',
    TAX_INPUT_NUMERIC_INPUT: 'initial-payment-tax-surcharge-numeric-input',
    TAX_AMOUNT_NUMERIC_INPUT: 'initial-payment-consumption-tax-amount-numeric-input',
    MONTH_TAX_NUMERIC_INPUT: 'monthly-payment-tax-included-numeric-input',
    SURCHARGE_INPUT_NUMERIC_INPUT: 'monthly-payment-tax-surcharge-numeric-input',
    PAY_NUMERIC_INPUT: 'monthly-payment-consumption-tax-amount-numeric-input',
    FINAL_PAYMENT_NUMERIC_INPUT: 'final-payment-tax-included-amount-numeric-input',
    TAX_NUMERIC_INPUT: 'final-payment-tax-surcharge-numeric-input',
    PAYMENT_FINAL_NUMERIC_INPUT: 'final-payment-consumption-tax-amount-numeric-input',
    DESIRED_DATE_INPUT: 'desired-delivery-start-date-date-input',

    INTERNAL_DOCUMENTS_FILE_UPLOAD: 'internal-documents-hue-drive-file-upload',
    IVTL_FILE_UPLOAD: 'item-ivtl-file-attachment-list',
    ATTACHMENT_TEXT_LABEL: 'attachment-text-label',
    INTERNAL_TWO_DOCUMENTS_FILE_UPLOAD: 'internal-two-documents-hue-drive-file-upload',
    IVTL_FILE_TWO_UPLOAD: 'item-ivtl-file-two-attachment-list',
    ATTACHMENT_TEXT_TWO_LABEL: 'attachment-two-text-label',
    GO_TO_CONFIRM_BUTTON: 'btn-go-confirmation',
    GO_TO_CONFIRM_BUTTON_FOOTER: 'btn-go-confirmation-footer',
    DETAIL_TOASTR: 'detail-toastr',
    FILE_ONE_PREVIEW: 'file-one-preview',
    FILE_TWO_PREVIEW: 'file-two-preview'
  };


  /**
   * eventEnum_ is an Enum of events.
   *
   * @private
   * @enum {string}
   */
  purchaseOrderApplicationInput.EVENT_ENUM_ = {
    BUTTON_ACTION: wap.core.ui.WapButton.EventType.ACTION,
    ANCHOR_LABEL: wap.core.ui.WapAnchorLabel.EventType.ACTION,
    GRID_CELL_CHANGE: wap.core.ui.sima.Sheet.EventType.CELLS_CHANGED,
    FILE_UPLOADED: wap.core.ui.hueDrive.FileUpload.EventType.FILE_UPLOADED,
    FILE_UPLODER_SPLIT: wap.core.ui.hueDrive.FileSelector.EventType.ITEM_SELECTED,
    SPLITER_ACTION: wap.core.ui.WapSplitButton.EventType.ACTION,
    SINGLE_SELECT: wap.core.ui.sima.editor.SingleSelectEditor,
    MASTER_ACTION: wap.core.ui.WapMasterInput.EventType.CHANGE,
    REMOVE_TAG: wap.core.ui.WapTagLabel.EventType.REMOVE,
    TYPEAHEAD: wap.core.ui.WapTextInput.EventType.TYPEAHEAD,
    CHANGE: wap.core.ui.WapSingleSelect.EventType.CHANGE,
    NUMERIC_INPUT_CHANGE: wap.core.ui.WapNumericInput.EventType.CHANGE,
    DATE_CHANGE: wap.core.ui.WapDateInput.EventType.CHANGE,
    UPLOAD_COMPLETED: wap.core.ui.hueDrive.FileUpload.EventType.UPLOAD_COMPLETED,
    CANCELCLICK: wap.ivtl.ui.IvtlFileAttachmentList.EventType.ON_CLOSE,
    FILE_CLICK: wap.ivtl.ui.IvtlFileAttachmentList.EventType.ON_FILE_CLICK
  };

  /**
   * constants_ is an Enum for constants.
   *
   * @private
   * @enum {String}
   */
  purchaseOrderApplicationInput.CONSTANTS_ = {
    TAX_RATE: 8,
    VALID_FLAG: false,
    EMPTY_STRING: '',
    NUMERIC_ZERO: 0,
    NUMERIC_ONE: 1,
    NUMERIC_TWO: 2,
    NUMERIC_FOUR: 4,
    NUMERIC_FIVE: 5,
    NUMERIC_NINE: 9,
    NUMERIC_TEN: 10,
    NUMERIC_ELEVEN: 11,
    NUMERIC_TWELVE: 12,
    NUMERIC_HUNDRED: 100,
    TOTAL_BYTES: 1024,
    ZERO_VALUE: '0',
    ONE_VALUE: '1',
    TWO_VALUE: '2',
    INPUT_FORM: 'input-form',
    ATTACHED_FROM_PC: 'attach_from_pc',
    MAIL_ATTACHED: 'mail_attachment',
    ENTER_DETAILED_INFORMATION_ON_PAYMENT: 'PC.POMT.enterDetailedInformation',
    CLOSE_PREPAYMENT_DETAILS: 'PC.POMT.closePrepaymentDetails',
    COLUMN: 'column',
    DATA: 'data',
    CELLS: 'cells',
    ROW: 'row',
    COLOUMN: 'coloumn',
    POST: 'POST',
    TARGET_KEY: 'targetKey',
    VALUE: 'value',
    NAME: 'name',
    PORTAL_URL: '/hue/scm/procurement/ordermanagement/ordermanagement/portal/ordermanagement/index',
    USER_FILE_ID: 'userFileId',
    FILE_NAME: 'fileName',
    FILE_SIZE: 'fileSize',
    FILE_ID: 'fileId',
    USER_ID: 'userId',
    TIME_UUID: 'timeUUID',
    ID: 'id',
    PC_POMT_NEEDSACTUALMEASUREMENT: 'PC.POMT.NeedsActualMeasurement',
    PC_POMT_NONEEDS: 'PC.POMT.NoNeeds',
    PC_POMT_POSSIBLE: 'PC.POMT.Possible',
    PC_POMT_IMPOSSIBLE: ' PC.POMT.Impossible',
    PC_POMT_EXCLUDED: 'PC.POMT.Excluded',
    PC_POMT_INCLUDED: 'PC.POMT.Included',
    ITEM_NAME_VALUE: 'itemNameValue',
    INTERNAL_DOCUMENTS_LIST: 'internalDocumentsList',
    SET_EVIDENCE_DETAILS: 'setEvidenceDetails',
    REMOVE_EVIDENCE_DETAILS: 'removeEvidenceDetails',
    JPY: 'JPY',
    PC_POMT_EXCLUDINGTAX: 'PC.POMT.ExcludingTax',
    PC_POMT_INCLUDINGTAX: 'PC.POMT.IncludingTax',
    PC_POMT_TAXEXEMPT: 'PC.POMT.TaxExempt',
    KB: 'KB',
    MB: 'MB',
    ONEKB: '1KB',
    EXCLUDING_TAX: 'EXCLUDING_TAX',
    INCLUDING_TAX: 'INCLUDING_TAX',
    TAX_EXEMPT: 'TAX_EXEMPT',
    INFO_MESSAGE_TOASTR: 'INVN.GMMT.duplicateAttach'
  };

  /**
   * @override
   *
   * enterDocument method Bind's the event of the screen.
   */
  purchaseOrderApplicationInput.prototype.enterDocument = function() {
    goog.base(this, 'enterDocument');
    purchaseOrderApplicationInputDao.clearOldFiles();
    this.initializeComponents_();
    this.setEmptyMasterInputSubItemValue_();
    this.clearMLStringMasterInput_();
    this.setMasterInputSubItemValue_();
    var eventEnums = {};
    this.lineEraseButton_ = this.getComponent_(this.idEnum_.LINE_ERASE_BUTTON);
    var taxValue = purchaseOrderApplicationInput.CONSTANTS_.TAX_RATE;
    this.applicationIdComponent_ = this.getComponent_(this.idEnum_.APPLICATION_ID);
    var uri = new goog.Uri(goog.dom.getWindow().location.href);
    var eventEnums = purchaseOrderApplicationInput.EVENT_ENUM_;
    this.applicationIdComponent_.setValue(uri.getQueryData().get('appId'));
    var column = this.orderApplicationLineDetailsSimaGrid_.getColumn(this.idEnum_.DESIRED_DELIVERY_START_DATE_SIMA_GRID_COLUMN);
    var columnDate = this.orderApplicationLineDetailsSimaGrid_.getColumn(this.idEnum_.DESIRED_DELIVERY_DATE_TIME_SIMA_GRID_COLUMN);
    var editorOptions = column.get('cell:editorOptions');
    var date = new goog.date.Date();
    var month = date.getMonth() + 1;
    var idEnums = purchaseOrderApplicationInput.ID_ENUM_;

    this.listen_(this.lineEraseButton_,
            eventEnums.BUTTON_ACTION, this.clear_);

    this.getHandler().listen(goog.global, goog.events.EventType.BEFOREUNLOAD, function(event) {
      if (purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG) {
        event.getBrowserEvent().returnValue = purchaseOrderApplicationInput.CONSTANTS_.EMPTY_STRING;
      }
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.ORDER_SUBJECT_TEXT_INPUT), eventEnums.TYPEAHEAD,
      function(event) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.CHARGE_ORDER_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.PURCHASE_ORDER_TEXT_INPUT), eventEnums.TYPEAHEAD,
      function(event) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.PURCHASE_FAXNUMBER_TEXT_INPUT), eventEnums.TYPEAHEAD,
      function(event) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.PURCHASE_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.DELIVER_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.ADDRESS_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.ORDER_REMARKS_TEXT_INPUT), eventEnums.TYPEAHEAD,
      function(event) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.CASE_TEXT_INPUT), eventEnums.TYPEAHEAD, function(event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.SERVICE_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.TELEPHONE_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.FAX_TEXT_INPUT), eventEnums.TYPEAHEAD, function(event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.NUMBER_TEXT_INPUT), eventEnums.TYPEAHEAD, function(event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });


    this.listen_(this.viewInstance_.getComponent(idEnums.REQUEST_NUMBER_TEXT_INPUT), eventEnums.TYPEAHEAD,
      function(event) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.PURPOSE_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.CLIENT_TEXT_INPUT), eventEnums.TYPEAHEAD, function(event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.REQUEST_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.RECURITMENT_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.REMARKS_TEXT_INPUT), eventEnums.TYPEAHEAD, function(
      event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });

    this.listen_(this.viewInstance_.getComponent(idEnums.ORDER_TEXT_INPUT), eventEnums.TYPEAHEAD, function(event) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });





    this.listen_(this.viewInstance_.getComponent(idEnums.ORDERING_SINGLE_SELECT), eventEnums.CHANGE, function(e) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.SUBCONTRACT_SINGLE_SELECT), eventEnums.CHANGE, function(
      e) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.SHIPPING_SINGLE_SELECT), eventEnums.CHANGE, function(e) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.SCHEDULED_SINGLE_SELECT), eventEnums.CHANGE, function(e) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.POSSIBILITY_SINGLE_SELECT), eventEnums.CHANGE, function(
      e) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.REQUIRED_SINGLE_SELECT), eventEnums.CHANGE, function(e) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.CONTRACT_SINGLE_SELECT), eventEnums.CHANGE, function(e) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });



    this.listen_(this.viewInstance_.getComponent(idEnums.PREPAYMENT_MONEY_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.CONSUMPTION_TAX_MONEY_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.TAX_INCLUDED_MONEY_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.TOTAL_SALE_MONEY_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });


    this.listen_(this.viewInstance_.getComponent(idEnums.ORDER_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.MONTH_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;

      });
    this.listen_(this.viewInstance_.getComponent(idEnums.PAYMENT_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;

      });
    this.listen_(this.viewInstance_.getComponent(idEnums.FIRST_PAYMENT_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;

      });
    this.listen_(this.viewInstance_.getComponent(idEnums.TAX_INPUT_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;

      });
    this.listen_(this.viewInstance_.getComponent(idEnums.MONTH_TAX_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });

    this.listen_(this.viewInstance_.getComponent(idEnums.SURCHARGE_INPUT_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;

      });

    this.listen_(this.viewInstance_.getComponent(idEnums.PAY_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });

    this.listen_(this.viewInstance_.getComponent(idEnums.FINAL_PAYMENT_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });

    this.listen_(this.viewInstance_.getComponent(idEnums.TAX_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });
    this.listen_(this.viewInstance_.getComponent(idEnums.PAYMENT_FINAL_NUMERIC_INPUT), eventEnums.NUMERIC_INPUT_CHANGE,
      function(e) {
        purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
      });



    this.listen_(this.viewInstance_.getComponent(idEnums.ORDER_DATE_INPUT), eventEnums.DATE_CHANGE, function(e) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });
    this.listen_(this.viewInstance_.getComponent(idEnums.DESIRED_DATE_INPUT), eventEnums.DATE_CHANGE, function(e) {
      purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    });


    if (month < 10) {
      month = '0' + month;
    }
    
    var todaysDate = date.getDate();
    todaysDate = todaysDate - 1;

    var format = date.getFullYear() + '/' + month + '/' + todaysDate;
    editorOptions['minDate'] = format;
    column.set('cell:editorOptions', editorOptions);
    columnDate.set('cell:editorOptions', editorOptions);
    this.orderApplicationLineDetailsSimaGrid_.updateColumn(this.idEnum_
      .DESIRED_DELIVERY_START_DATE_SIMA_GRID_COLUMN, null, {
        'cell:editor': 'wap.core.ui.zhuge.editor.DateEditor',
        'cell:editorOptions': editorOptions
      });

    this.orderApplicationLineDetailsSimaGrid_.updateColumn(this.idEnum_
      .DESIRED_DELIVERY_DATE_TIME_SIMA_GRID_COLUMN, null, {
        'cell:editor': 'wap.core.ui.zhuge.editor.DateEditor',
        'cell:editorOptions': editorOptions
      });



    this.updateGridColumns_();
    this.prepaymentTermsRow_ = this.getComponent_(this.idEnum_.PREPAYMENT_TERMS_ROW);

    this.prepaidExpensesTypeRow_ = this.getComponent_(this.idEnum_.PREPAID_EXPENSES_TYPE_ROW);

    var $dashboardElementOne = this.prepaymentTermsRow_.getElement();
    wap.core.util.style.setElementShown($dashboardElementOne, false);

    var $dashboardElement = this.prepaidExpensesTypeRow_.getElement();
    wap.core.util.style.setElementShown($dashboardElement, false);

    if (this.consumptionTaxAmount_.getValue() === 0) {
      this.consumptionTaxAmount_.clear();
      this.taxIncludedAmount_.clear();
      this.taxWithdrawalAmount_.clear();
      this.prepaymentAmountMoneyInput_.clear();
    }

    // goog.dom.getElement(this.idEnum_.FILE_TAGS_INPUT_TARGET).style.borderStyle = 'none';
    // goog.dom.getElement(this.idEnum_.INHOUSE_TAGS_INPUT_TARGET).style.borderStyle = 'none';


    this.listen_(this.enterDetailInformationPaymentAnchorLabel_, this.eventEnum_.ANCHOR_LABEL, this.openPaymentDetails_);


    this.listen_(this.returnButton_, this.eventEnum_.BUTTON_ACTION, this.backToPortal_);

    this.listen_(this.lineExpandButton_, this.eventEnum_.BUTTON_ACTION, this.expandOrderApplicationDetailsGrid_);

    this.listen_(this.lineContractButton_, this.eventEnum_.BUTTON_ACTION, this.contractOrderApplicationDetailsGrid_);
    //    this.listen_(this.fileTags_, this.eventEnum_.REMOVE_TAG, function(event) {
    //      var param;
    //      param = this.buttonClicked_;
    //      this.removeFileCancelled_(event, param);
    //    });
    //    this.listen_(this.inhousefileTags_, this.eventEnum_.REMOVE_TAG, function(event) {
    //      var param;
    //      param = this.buttonClicked_;
    //      this.removeFileCancelled_(event, param);
    //    });


    this.listen_(this.orderApplicationLineDetailsSimaGrid_.getSheet(), this.eventEnum_.GRID_CELL_CHANGE, function(
      event) {
      var activeRowIndex = this.orderApplicationLineDetailsSimaGrid_.getActiveRowIndex();
      var getRowIdValue = this.orderApplicationLineDetailsSimaGrid_.getRowId(activeRowIndex);
      var rowData = this.orderApplicationLineDetailsSimaGrid_.getRow(activeRowIndex);
      this.orderApplicationLineDetailsSimaGrid_.showPopover_();
      
      var sheet = this.orderApplicationLineDetailsSimaGrid_.getSheet();
      if (!rowData['itemNameValue']) {
        sheet.setCellValue(activeRowIndex + this.constants_.NUMERIC_ONE, this.constants_.NUMERIC_ONE, this.constants_
          .EMPTY_STRING);
      }
      if (event.data.cells[this.constants_.NUMERIC_ZERO][this.constants_.COLUMN] === this.constants_.NUMERIC_ONE) {
        this.updateGridData_(event);
      }
      if (event.data.cells[this.constants_.NUMERIC_ZERO][this.constants_.COLUMN] === this.constants_.NUMERIC_NINE) {
        this.insertGridData_();
      }
      var totalQuantity = this.orderApplicationLineDetailsSimaGrid_
        .getCellValue(getRowIdValue, this.idEnum_.QUANTITY_SIMA_GRID_COLUMN);
      var paymentValue = this.orderApplicationLineDetailsSimaGrid_
        .getCellValue(getRowIdValue, this.idEnum_.UNIT_PRICE_SIMA_GRID_COLUMN);
      var taxValue = this.orderApplicationLineDetailsSimaGrid_
        .getCellValue(getRowIdValue, this.idEnum_.SALES_TAX_RATE_SIMA_GRID_COLUMN);
      if (totalQuantity < 0) {
        totalQuantity = purchaseOrderApplicationInput.CONSTANTS_.EMPTY_STRING;
        this.orderApplicationLineDetailsSimaGrid_
          .updateCellAt(activeRowIndex, this.idEnum_.QUANTITY_SIMA_GRID_COLUMN,
            purchaseOrderApplicationInput.CONSTANTS_.EMPTY_STRING, true, false);
      }
      if (paymentValue < 0) {
        paymentValue = purchaseOrderApplicationInput.CONSTANTS_.EMPTY_STRING;
        this.orderApplicationLineDetailsSimaGrid_
          .updateCellAt(activeRowIndex, this.idEnum_.UNIT_PRICE_SIMA_GRID_COLUMN,
            purchaseOrderApplicationInput.CONSTANTS_.EMPTY_STRING, true, false);
      }
      var gridRowCount = this.orderApplicationLineDetailsSimaGrid_
        .getRowsCount();
      var data = this.constants_.EMPTY_STRING;
      var totalValue = paymentValue * totalQuantity;
      if (totalValue) {
        var consumptionTax = totalValue * (this.constants_.NUMERIC_ONE + (taxValue / this.constants_.NUMERIC_HUNDRED));
        this.orderApplicationLineDetailsSimaGrid_
          .updateCellAt(activeRowIndex, this.idEnum_.TAX_INCLUDED_AMOUNT_SIMA_GRID_COLUMN, consumptionTax,
            true,
            false);
      } else {
        this.orderApplicationLineDetailsSimaGrid_
          .updateCellAt(activeRowIndex, this.idEnum_.TAX_INCLUDED_AMOUNT_SIMA_GRID_COLUMN, data,
            true,
            false);
      }
      var quantityValue = paymentValue * totalQuantity;

      if (quantityValue) {
        var consumptionNewTax = totalValue * (taxValue / this.constants_.NUMERIC_HUNDRED);
        this.orderApplicationLineDetailsSimaGrid_
          .updateCellAt(activeRowIndex, this.idEnum_.CONSUMPTION_TAX_SIMA_GRID_COLUMN,
            consumptionNewTax, true, false);
      } else {
        this.orderApplicationLineDetailsSimaGrid_
          .updateCellAt(activeRowIndex, this.idEnum_.CONSUMPTION_TAX_SIMA_GRID_COLUMN,
            data, true, false);
      }
      var totalNewQuantityValue = paymentValue * totalQuantity;

      if (totalNewQuantityValue) {
        var taxExcluded = totalNewQuantityValue;
        this.orderApplicationLineDetailsSimaGrid_
          .updateCellAt(activeRowIndex, this.idEnum_.TAX_EXCLUDED_AMOUNT_SIMA_GRID_COLUMN,
            taxExcluded, true, false);
      } else {
        this.orderApplicationLineDetailsSimaGrid_
          .updateCellAt(activeRowIndex, this.idEnum_.TAX_EXCLUDED_AMOUNT_SIMA_GRID_COLUMN,
            data, true, false);
      }
      var totalAcquisitionPrice = goog.string.parseInt(this.constants_.ZERO_VALUE);
      var totalConsumptionTaxPrice = goog.string.parseInt(this.constants_.ZERO_VALUE);
      for (var index = this.constants_.NUMERIC_ZERO; index < gridRowCount; index++) {
        var iteratorRowId = this.orderApplicationLineDetailsSimaGrid_
          .getRowId(index);
        var iteratorPaymentValue = this.orderApplicationLineDetailsSimaGrid_
          .getCellValue(
            iteratorRowId,
            this.idEnum_.TAX_EXCLUDED_AMOUNT_SIMA_GRID_COLUMN);
        if (iteratorPaymentValue) {
          totalAcquisitionPrice = totalAcquisitionPrice + iteratorPaymentValue;
        }
        var iteratorConsumptionTax = this.orderApplicationLineDetailsSimaGrid_
          .getCellValue(
            iteratorRowId,
            this.idEnum_.CONSUMPTION_TAX_SIMA_GRID_COLUMN);
        if (iteratorConsumptionTax) {
          totalConsumptionTaxPrice = totalConsumptionTaxPrice + iteratorConsumptionTax;
        }
      }

      var consumptionNewTaxFinal = totalValue * (taxValue / this.constants_.NUMERIC_HUNDRED);
      var totalAmount = totalAcquisitionPrice * (this.constants_.NUMERIC_ONE + (purchaseOrderApplicationInput
        .CONSTANTS_.TAX_RATE / this.constants_.NUMERIC_HUNDRED));
      this.totalSaleAmountMoneyInput_.setValue(totalAmount
        .toString());
      if (totalConsumptionTaxPrice) {
        this.consumptionTaxAmount_.setValue(totalConsumptionTaxPrice);
      } else {
        this.consumptionTaxAmount_.clear();
      }
      if (totalAmount) {
        this.taxIncludedAmount_.setValue(totalAmount
          .toString());
      } else {
        this.taxIncludedAmount_.clear();
      }
      if (totalAcquisitionPrice) {
        this.taxWithdrawalAmount_.setValue(totalAcquisitionPrice
          .toString());

      } else {
        this.taxWithdrawalAmount_.clear();
      }



    });
    this.getCurrency_();

    this.listen_(this.projectMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.prepaymentSubjectMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.installationLocationMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.deliveryDestinationMaster_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.acceptingDepartmentMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.receivingPersonMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.packingMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.deliveryConditionMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.orderResponsibleDepartmentMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.transferSourceResponsibleDepartmentMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.vendorMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.paymentTermsMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.transferSourceResponsibleDepartmentMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.prepaidExpensesTypeMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.inspectionDepartmentMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.acceptanceDepartmentMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.burdenDepartmentMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.budgetInformationMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.costClassificationMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.assetClassificationMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.transferringPersonInChargeMasterInput_, this.eventEnum_.MASTER_ACTION, this.masterClear_);
    this.listen_(this.sendingMethodMasterInput, this.eventEnum_.MASTER_ACTION, this.masterClear_);



    this.listen_(this.attachedFilesUpload_, this.eventEnum_.FILE_UPLOADED, this.loadFileUploaded);
    this.listen_(this.ivtlAttachedFilesUpload_, this.eventEnum_.CANCELCLICK, this.removeFileCancelled);
    this.listen_(this.attachedFilesUpload_, this.eventEnum_.UPLOAD_COMPLETED, this.uploadCompleted);

    this.listen_(this.attachedTwoFilesUpload_, this.eventEnum_.FILE_UPLOADED, this.loadTwoFileUploaded);
    this.listen_(this.ivtlAttachedTwoFilesUpload_, this.eventEnum_.CANCELCLICK, this.removeTwoFileCancelled);
    this.listen_(this.attachedTwoFilesUpload_, this.eventEnum_.UPLOAD_COMPLETED, this.uploadTwoCompleted);


    if (this.ivtlAttachedFilesUpload_) {
      this.listen_(this.ivtlAttachedFilesUpload_, purchaseOrderApplicationInput.EVENT_ENUM_.FILE_CLICK,
        function(event) {
          this.doFilePreview_(event, this.constants_.NUMERIC_ONE);
        });
    }
    if (this.ivtlAttachedTwoFilesUpload_) {
      this.listen_(this.ivtlAttachedTwoFilesUpload_, purchaseOrderApplicationInput.EVENT_ENUM_.FILE_CLICK,
        function(event) {
          this.doFilePreview_(event, this.constants_.NUMERIC_TWO);
        });
    }
  };


  /**
   * doFilePreview_ method to get the parameters from the on click to show file preview.
   *
   * @param {Object} event
   * @param {Object} fileOrder
   * @private
   */
  purchaseOrderApplicationInput.prototype.doFilePreview_ = function(event, fileOrder) {
    var selectedListItem = event['data'];
    var attachment = {
      'id': selectedListItem['fileProperties']['fileId'],
      'fileName': selectedListItem['fileProperties']['fileName'],
      'timeUUID': selectedListItem['fileProperties']['timeUUID'],
      'userId': selectedListItem['fileProperties']['userId']
    };
    this.loadFileFields(attachment, fileOrder);
  };


  /**
   * loadFileFields to show preview
   *
   * @param {Object} event
   * @param {Object} fileOrder
   */
  purchaseOrderApplicationInput.prototype.loadFileFields = function(event, fileOrder) {
    var fileName = event['fileName'];
    var fileId = event['id'];
    var timeUUID = event['timeUUID'];
    var userId = event['userId'];
    var filePreview;
    if (fileOrder === this.constants_.NUMERIC_ONE) {
      filePreview = this.firstFilePreview_;
    }
    if (fileOrder === this.constants_.NUMERIC_TWO) {
      filePreview = this.secondFilePreview_;
    }
    var previewItem = filePreview.generateNewPreviewItem(userId, fileId, timeUUID, fileName);
    filePreview.clearFileList();
    filePreview.appendToFileList(previewItem);
    filePreview.showPreview(previewItem);
  };



  /**
   * uploadCompleted is used to remove the progress bar when file is uploaded.
   *
   * @override
   */
  purchaseOrderApplicationInput.prototype.uploadCompleted = function() {
    this.getComponent_(this.idEnum_.INTERNAL_DOCUMENTS_FILE_UPLOAD).removeAndCloseProgressBarContainer();
    this.goToConfirmBtn_.enable();
    this.goToConfirmBtnFooter_.enable();
  };

  /**
   * uploadTwoCompleted is used to remove the progress bar when file is uploaded.
   *
   * @override
   */
  purchaseOrderApplicationInput.prototype.uploadTwoCompleted = function() {
    this.getComponent_(this.idEnum_.INTERNAL_TWO_DOCUMENTS_FILE_UPLOAD).removeAndCloseProgressBarContainer();
    this.goToConfirmBtn_.enable();
    this.goToConfirmBtnFooter_.enable();
  };

  /**
   * doFilePreview_ method to get the parameters from the on click to show file preview
   *
   * @param {Object} event
   * @param {Object} fileOrder
   * @private
   */
  purchaseOrderApplicationInput.prototype.doFilePreview_ = function(event, fileOrder) {
    var selectedListItem = event['data'];
    var attachment = {
      'id': selectedListItem['fileProperties']['fileId'],
      'fileName': selectedListItem['fileProperties']['fileName'],
      'timeUUID': selectedListItem['fileProperties']['timeUUID'],
      'userId': selectedListItem['fileProperties']['userId']
    };
    this.loadFileFields(attachment, fileOrder);
  };

  /**
   * exitDocument used to unbind the event.
   *
   * @override
   */
  purchaseOrderApplicationInput.prototype.exitDocument = function() {
    this.getHandler().removeAll();
    goog.base(this, 'exitDocument');
  };

  /**
   * loadFileUploaded sets the attached evidence details This method
   * passes the attachment to the PurchaseOrderApplicationInputDao where ajax
   * process takes place.
   *
   * @param {Event}
   *            event
   */
  purchaseOrderApplicationInput.prototype.loadFileUploaded = function(event) {
    var id = new goog.string.StringBuffer(
      event.uploadData['userFileId']['fileId'],
      event.uploadData['userFileId']['timeUUID']).toString();
    var fileNames = [];
    var attachment = {
      'id': id,
      'fileName': event.uploadData[this.constants_.FILE_NAME],
      'fileSize': wap.core.util.number.ceil(event.uploadData[this.constants_.FILE_SIZE] / this.constants_.TOTAL_BYTES),
      'userId': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.USER_ID],
      'timeUUID': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.TIME_UUID],
      'fileId': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.FILE_ID]
    };
    this.goToConfirmBtn_.disable();
    this.goToConfirmBtnFooter_.disable();
    var fileHistory = this.getComponent_(this.idEnum_.IVTL_FILE_UPLOAD).getFileDetails();
    goog.array.forEach(fileHistory, function(value, index) {
      fileNames[index] = value['fileName'];
    }, this);
    if (!(goog.array.contains(fileNames, attachment['fileName']))) {
      this.getComponent_(this.idEnum_.IVTL_FILE_UPLOAD).addItem(
        attachment['id'], attachment['fileName'], attachment['fileSize'], attachment);
      purchaseOrderApplicationInputDao.loadFileUploaded(attachment);
    } else {
      this.getText_(this.constants_.INFO_MESSAGE_TOASTR).then(
        goog.bind(function(infoText) {
          this.detailToastr.error(infoText);
        }, this));
    }
  };

  /**
   * loadTwoFileUploaded sets the attached evidence details This method
   * passes the attachment to the PurchaseOrderApplicationInputDao where ajax
   * process takes place.
   *
   * @param {Event}
   *            event
   */
  purchaseOrderApplicationInput.prototype.loadTwoFileUploaded = function(event) {
    var id = new goog.string.StringBuffer(
      event.uploadData['userFileId']['fileId'],
      event.uploadData['userFileId']['timeUUID']).toString();
    var fileNames = [];
    var attachment = {
      'id': id,
      'fileName': event.uploadData[this.constants_.FILE_NAME],
      'fileSize': goog.math.safeCeil(event.uploadData[this.constants_.FILE_SIZE] / this.constants_.TOTAL_BYTES),
      'userId': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.USER_ID],
      'timeUUID': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.TIME_UUID],
      'fileId': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.FILE_ID]
    };
    this.goToConfirmBtn_.disable();
    this.goToConfirmBtnFooter_.disable();
    var fileHistory = this.getComponent_(this.idEnum_.IVTL_FILE_TWO_UPLOAD).getFileDetails();
    goog.array.forEach(fileHistory, function(value, index) {
      fileNames[index] = value['fileName'];
    }, this);
    if (!(goog.array.contains(fileNames, attachment['fileName']))) {
      this.getComponent_(this.idEnum_.IVTL_FILE_TWO_UPLOAD).addItem(
        attachment['id'], attachment['fileName'], attachment['fileSize'], attachment);
      purchaseOrderApplicationInputDao.loadFileUploadedTwo(attachment);
    } else {
      this.getText_(this.constants_.INFO_MESSAGE_TOASTR).then(
        goog.bind(function(infoText) {
          this.detailToastr.error(infoText);
        }, this));
    }

  };

  /**
   * removeFileCancelled removed the attached evidence details This
   * method passes the attachment to the PurchaseOrderApplicationInputDao where
   * ajax process takes place.
   *
   * @param {Event}
   *            event
   */
  purchaseOrderApplicationInput.prototype.removeFileCancelled = function(event) {
    var listItem = event['data'];
    var attachment = {
      'id': listItem['id'],
      'fileName': listItem['fileName'],
      'fileSize': listItem['fileSize']
    };
    purchaseOrderApplicationInputDao.removeFileCancelled(attachment);
  };

  /**
   * removeTwoFileCancelled removed the attached evidence details This
   * method passes the attachment to the PurchaseOrderApplicationInputDao where
   * ajax process takes place.
   *
   * @param {Event}
   *            event
   */
  purchaseOrderApplicationInput.prototype.removeTwoFileCancelled = function(event) {
    var listItem = event['data'];
    var attachment = {
      'id': listItem['id'],
      'fileName': listItem['fileName'],
      'fileSize': listItem['fileSize']
    };
    purchaseOrderApplicationInputDao.removeSecondFileCancelled(attachment);
  };

  /**
   * loadFileFields to show preview
   *
   * @param {Object} event
   * @param {Object} fileOrder
   */
  purchaseOrderApplicationInput.prototype.loadFileFields = function(event, fileOrder) {
    var fileName = event['fileName'];
    var fileId = event['id'];
    var timeUUID = event['timeUUID'];
    var userId = event['userId'];
    var filePreview;
    if (fileOrder === this.constants_.NUMERIC_ONE) {
      filePreview = this.firstFilePreview_;
    }
    if (fileOrder === this.constants_.NUMERIC_TWO) {
      filePreview = this.secondFilePreview_;
    }
    var previewItem = filePreview.generateNewPreviewItem(userId, fileId, timeUUID, fileName);
    filePreview.clearFileList();
    filePreview.appendToFileList(previewItem);
    filePreview.showPreview(previewItem);
  };



  /**
   * uploadCompleted is used to remove the progress bar when file is uploaded.
   *
   * @override
   */
  purchaseOrderApplicationInput.prototype.uploadCompleted = function() {
    this.getComponent_(this.idEnum_.INTERNAL_DOCUMENTS_FILE_UPLOAD).removeAndCloseProgressBarContainer();
    this.goToConfirmBtn_.enable();
    this.goToConfirmBtnFooter_.enable();
  };

  /**
   * uploadTwoCompleted is used to remove the progress bar when file is uploaded.
   *
   * @override
   */
  purchaseOrderApplicationInput.prototype.uploadTwoCompleted = function() {
    this.getComponent_(this.idEnum_.INTERNAL_TWO_DOCUMENTS_FILE_UPLOAD).removeAndCloseProgressBarContainer();
    this.goToConfirmBtn_.enable();
    this.goToConfirmBtnFooter_.enable();
  };

  /**
   * exitDocument used to unbind the event.
   *
   * @override
   */
  purchaseOrderApplicationInput.prototype.exitDocument = function() {
    this.getHandler().removeAll();
    goog.base(this, 'exitDocument');
  };

  /**
   * loadFileUploaded sets the attached evidence details This method
   * passes the attachment to the PurchaseOrderApplicationInputDao where ajax
   * process takes place.
   *
   * @param {Event}
   *            event
   */
  purchaseOrderApplicationInput.prototype.loadFileUploaded = function(event) {
    var id = new goog.string.StringBuffer(
      event.uploadData['userFileId']['fileId'],
      event.uploadData['userFileId']['timeUUID']).toString();
    var fileNames = [];
    var attachment = {
      'id': id,
      'fileName': event.uploadData[this.constants_.FILE_NAME],
      'fileSize': wap.core.util.number.ceil(event.uploadData[this.constants_.FILE_SIZE] / this.constants_.TOTAL_BYTES),
      'userId': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.USER_ID],
      'timeUUID': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.TIME_UUID],
      'fileId': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.FILE_ID]
    };
    this.goToConfirmBtn_.disable();
    this.goToConfirmBtnFooter_.disable();
    var fileHistory = this.getComponent_(this.idEnum_.IVTL_FILE_UPLOAD).getFileDetails();
    goog.array.forEach(fileHistory, function(value, index) {
      fileNames[index] = value['fileName'];
    }, this);
    if (!(goog.array.contains(fileNames, attachment['fileName']))) {
      this.getComponent_(this.idEnum_.IVTL_FILE_UPLOAD).addItem(
        attachment['id'], attachment['fileName'], attachment['fileSize'], attachment);
      purchaseOrderApplicationInputDao.loadFileUploaded(attachment);
    } else {
      this.getText_(this.constants_.INFO_MESSAGE_TOASTR).then(
        goog.bind(function(infoText) {
          this.detailToastr.error(infoText);
        }, this));
    }

  };

  /**
   * loadTwoFileUploaded sets the attached evidence details This method
   * passes the attachment to the PurchaseOrderApplicationInputDao where ajax
   * process takes place.
   *
   * @param {Event}
   *            event
   */
  purchaseOrderApplicationInput.prototype.loadTwoFileUploaded = function(event) {
    var id = new goog.string.StringBuffer(
      event.uploadData['userFileId']['fileId'],
      event.uploadData['userFileId']['timeUUID']).toString();
    var fileNames = [];
    var attachment = {
      'id': id,
      'fileName': event.uploadData[this.constants_.FILE_NAME],
      'fileSize': goog.math.safeCeil(event.uploadData[this.constants_.FILE_SIZE] / this.constants_.TOTAL_BYTES),
      'userId': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.USER_ID],
      'timeUUID': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.TIME_UUID],
      'fileId': event.uploadData[this.constants_.USER_FILE_ID][this.constants_.FILE_ID]
    };
    this.goToConfirmBtn_.disable();
    this.goToConfirmBtnFooter_.disable();
    var fileHistory = this.getComponent_(this.idEnum_.IVTL_FILE_TWO_UPLOAD).getFileDetails();
    goog.array.forEach(fileHistory, function(value, index) {
      fileNames[index] = value['fileName'];
    }, this);
    if (!(goog.array.contains(fileNames, attachment['fileName']))) {
      this.getComponent_(this.idEnum_.IVTL_FILE_TWO_UPLOAD).addItem(
        attachment['id'], attachment['fileName'], attachment['fileSize'], attachment);
      purchaseOrderApplicationInputDao.loadFileUploadedTwo(attachment);
    } else {
      this.getText_(this.constants_.INFO_MESSAGE_TOASTR).then(
        goog.bind(function(infoText) {
          this.detailToastr.error(infoText);
        }, this));
    }

  };

  /**
   * removeFileCancelled removed the attached evidence details This
   * method passes the attachment to the PurchaseOrderApplicationInputDao where
   * ajax process takes place.
   *
   * @param {Event}
   *            event
   */
  purchaseOrderApplicationInput.prototype.removeFileCancelled = function(event) {
    var listItem = event['data'];
    var attachment = {
      'id': listItem['id'],
      'fileName': listItem['fileName'],
      'fileSize': listItem['fileSize']
    };
    purchaseOrderApplicationInputDao.removeFileCancelled(attachment);
  };

  /**
   * removeTwoFileCancelled removed the attached evidence details This
   * method passes the attachment to the PurchaseOrderApplicationInputDao where
   * ajax process takes place.
   *
   * @param {Event}
   *            event
   */
  purchaseOrderApplicationInput.prototype.removeTwoFileCancelled = function(event) {
    var listItem = event['data'];
    var attachment = {
      'id': listItem['id'],
      'fileName': listItem['fileName'],
      'fileSize': listItem['fileSize']
    };
    purchaseOrderApplicationInputDao.removeSecondFileCancelled(attachment);
  };

  /**
   * initializeComponents_ is used to initialize the components.
   *
   * @private
   */
  purchaseOrderApplicationInput.prototype.initializeComponents_ = function() {
    this.viewInstance_ = wap.core.app.View.getInstance();

    this.idEnum_ = purchaseOrderApplicationInput.ID_ENUM_;
    this.eventEnum_ = purchaseOrderApplicationInput.EVENT_ENUM_;
    this.constants_ = purchaseOrderApplicationInput.CONSTANTS_;

    this.returnButton_ = this
      .getComponent_(this.idEnum_.RETURN_BUTTON);


    this.lineExpandButton_ = this
      .getComponent_(this.idEnum_.LINE_EXPAND_BUTTON);

    this.lineContractButton_ = this
      .getComponent_(this.idEnum_.LINE_CONTRACT_BUTTON);

    this.orderApplicationLineDetailsSimaGrid_ = this
      .getComponent_(this.idEnum_.ORDER_APPLICATION_LINE_DETAILS_SIMA_GRID);
    this.estimateNotSelectDetailsSimaGrid_ = this
      .getComponent_(this.idEnum_.ESTIMATE_NOT_SELECT_DETAILS_SIMA_GRID);

    this.totalSaleAmountMoneyInput_ = this
      .getComponent_(this.idEnum_.TOTAL_SALE_AMOUNT_MONEY_INPUT);


    this.consumptionTaxAmount_ = this
      .getComponent_(this.idEnum_.CONSUMPTION_TAX_AMOUNT_MONEY_INPUT);

    this.taxIncludedAmount_ = this
      .getComponent_(this.idEnum_.TAX_INCLUDED_AMOUNT_MONEY_INPUT);

    this.taxWithdrawalAmount_ = this
      .getComponent_(this.idEnum_.TAX_WITHDRAWAL_AMOUNT_MONEY_INPUT);


    this.enterDetailInformationPaymentAnchorLabel_ = this
      .getComponent_(this.idEnum_.ENTER_DETAIL_INFORMATION_PAYMENT_ANCHOR_LABEL);

    this.uploaderFromPcHueDriveFileUpload_ = this.
    getComponent_(this.idEnum_.UPLOADER_FROM_PC_HUE_DRIVE_FILE_UPLOAD);

    this.mailAttachmentHueDriveFileSelector_ = this.getComponent_(this.idEnum_.MAIL_ATTACHMENT_HUE_DRIVE_FILE_SELECTOR);

    this.sharedAttachmentHueDriveFileSelector_ = this
      .getComponent_(this.idEnum_.SHARED_ATTACHMENT_HUE_DRIVE_FILE_SELECTOR);

    this.constructionAttachmentSplitButton_ = this
      .getComponent_(this.idEnum_.CONSTRUCTION_ATTACHMENT_SPLIT_BUTTON);

    this.quotationOverviewSplitButton_ = this
      .getComponent_(this.idEnum_.QUOTATION_OVERVIEW_SPLIT_BUTTON);

    this.prepaymentTermsRow_ = this
      .getComponent_(this.idEnum_.PREPAYMENT_TERMS_ROW);

    this.prepaidExpensesTypeRow_ = this
      .getComponent_(this.idEnum_.PREPAID_EXPENSES_TYPE_ROW);

    this.prepaymentAmountMoneyInput_ = this
      .getComponent_(this.idEnum_.PREPAYMENT_AMOUNT_MONEY_INPUT);



    this.commonObject_ = new wap.scm.procurement.common.ScmCommon();

    this.lineEraseButton_ = this.getComponent_(this.idEnum_.LINE_ERASE_BUTTON);

    this.desiredDeliverydate_ = this.getComponent_(this.idEnum_.DESIRED_DELIVERY_DATE_INPUT);
    this.desiredDeliveryStartDate_ = this.getComponent_(this.idEnum_.DESIRED_DELIVERY_START_DATE_INPUT);
    this.orderDesiredDate_ = this.getComponent_(this.idEnum_.ORDER_DESIRED_DATE_INPUT);
    this.orderDate_ = this.getComponent_(this.idEnum_.ORDER_DATE_INPUT);
    //    this.fileTags_ = this.getComponent_(this.idEnum_.FILE_TAGS_INPUT);
    //    this.inhousefileTags_ = this.getComponent_(this.idEnum_.INHOUSE_TAGS_INPUT);

    this.currencyNameLabel_ = this.getComponent_(this.idEnum_.CURRENCY_NAME_LABEL);
    this.currencySignLabel_ = this.getComponent_(this.idEnum_.CURRENCY_SIGN_LABEL);

    this.projectMasterInput_ = this.getComponent_(this.idEnum_.PROJECT_MASTER_INPUT);
    this.prepaymentSubjectMasterInput_ = this.getComponent_(this.idEnum_.PREPAYMENT_SUBJECT_MASTER_INPUT);
    this.installationLocationMasterInput_ = this.getComponent_(this.idEnum_.INSTALLATION_LOCATION_MASTER_INPUT);
    this.deliveryDestinationMaster_ = this.getComponent_(this.idEnum_.DELIVERY_DESTINATION_MASTER_INPUT);
    this.acceptingDepartmentMasterInput_ = this.getComponent_(this.idEnum_.ACCEPTING_DEPARTMENT_MASTER_INPUT);
    this.receivingPersonMasterInput_ = this.getComponent_(this.idEnum_.RECEIVING_PERSON_MASTER_INPUT);
    this.packingMasterInput_ = this.getComponent_(this.idEnum_.PACKING_MASTER_INPUT);
    this.deliveryConditionMasterInput_ = this.getComponent_(this.idEnum_.DELIVERY_CONDITION_MASTER_INPUT);
    this.orderResponsibleDepartmentMasterInput_ = this.getComponent_(this.idEnum_.ORDER_RESPONSIBLE_DEPARTMENT_MASTER_INPUT);
    this.transferSourceResponsibleDepartmentMasterInput_ =
      this.getComponent_(this.idEnum_.TRANSFER_SOURCE_RESPONSIBLE_DEPARTMENT_MASTER_INPUT);
    this.vendorMasterInput_ = this.getComponent_(this.idEnum_.VENDOR_MASTER_INPUT);
    this.paymentTermsMasterInput_ = this.getComponent_(this.idEnum_.PAYMENT_TERMS_MASTER_INPUT);
    this.prepaidExpensesTypeMasterInput_ = this.getComponent_(this.idEnum_.PREPAID_EXPENSES_TYPE_MASTER_INPUT);
    this.inspectionDepartmentMasterInput_ = this.getComponent_(this.idEnum_.INSPECTION_DEPARTMENT_MASTER_INPUT);
    this.acceptanceDepartmentMasterInput_ = this.getComponent_(this.idEnum_.ACCEPTANCE_DEPARTMENT_MASTER_INPUT);
    this.burdenDepartmentMasterInput_ = this.getComponent_(this.idEnum_.BURDEN_DEPARTMENT_MASTER_INPUT);
    this.budgetInformationMasterInput_ = this.getComponent_(this.idEnum_.BUDGET_INFORMATION_MASTER_INPUT);
    this.costClassificationMasterInput_ = this.getComponent_(this.idEnum_.COST_CLASSIFICATION_MASTER_INPUT);
    this.assetClassificationMasterInput_ = this.getComponent_(this.idEnum_.ASSET_CLASSIFICATION_MASTER_INPUT);
    this.transferringPersonInChargeMasterInput_ = this.getComponent_(this.idEnum_.TRANSFERRING_PERSON_IN_CHARGE_MASTER_INPUT);
    this.sendingMethodMasterInput = this.getComponent_(this.idEnum_.SENDING_METHOD_MASTER_INPUT);
    this.desiredDeliveryDateTimeSimaGridColumn_ = this.getComponent_(this.idEnum_.DESIRED_DELIVERY_DATE_TIME_SIMA_GRID_COLUMN);
    this.desiredDeliveryStartDateSimaGridColumn_ = this.getComponent_(this.idEnum_.DESIRED_DELIVERY_START_DATE_SIMA_GRID_COLUMN);
    this.quotationReplyAmountMoneyInput_ = this.getComponent_(this.idEnum_.QUOTATION_REPLY_AMOUNT_MONEY_INPUT);
    this.quotationReplyAmountSimaGridColumn_ = this.getComponent_(this.idEnum_.QUOTATION_REPLY_AMOUNT_SIMA_GRID_COLUMN);


    /**
     * @private
     * @type {wap.core.ui.WapButton}
     */
    this.goToConfirmBtn_ = this.getComponent_(this.idEnum_.GO_TO_CONFIRM_BUTTON);

    /**
     * @private
     * @type {wap.core.ui.WapButton}
     */
    this.goToConfirmBtnFooter_ = this.getComponent_(this.idEnum_.GO_TO_CONFIRM_BUTTON_FOOTER);

    this.detailToastr = this.getComponent_(this.idEnum_.DETAIL_TOASTR);

    /**
     * @private
     * @type {wap.core.ui.WapSimaGrid}
     */
    this.attachedFilesUpload_ = this.getComponent_(this.idEnum_.INTERNAL_DOCUMENTS_FILE_UPLOAD);
    /**
     * @private
     * @type {wap.core.ui.WapSimaGrid}
     */
    this.ivtlAttachedFilesUpload_ = this.getComponent_(this.idEnum_.IVTL_FILE_UPLOAD);

    /**
     * @private
     * @type {wap.core.ui.WapSimaGrid}
     */
    this.attachedTwoFilesUpload_ = this.getComponent_(this.idEnum_.INTERNAL_TWO_DOCUMENTS_FILE_UPLOAD);
    /**
     * @private
     * @type {wap.core.ui.WapSimaGrid}
     */
    this.ivtlAttachedTwoFilesUpload_ = this.getComponent_(this.idEnum_.IVTL_FILE_TWO_UPLOAD);

    this.firstFilePreview_ = this.getComponent_(this.idEnum_.FILE_ONE_PREVIEW);
    this.secondFilePreview_ = this.getComponent_(this.idEnum_.FILE_TWO_PREVIEW);
  };

  /**
   *  masterClear_ is used for clearing master values.
   *  @private
   *  @param {Object} event
   */
  purchaseOrderApplicationInput.prototype.masterClear_ = function(event) {
    purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
    var constantsEnum = purchaseOrderApplicationInput.CONSTANTS_;
    (!this.projectMasterInput_.getValue() ? this.projectMasterInput_.clear() : constantsEnum.EMPTY_STRING);
    (!this.prepaymentSubjectMasterInput_.getValue() ? this.prepaymentSubjectMasterInput_.clear() : constantsEnum.EMPTY_STRING);
    (!this.installationLocationMasterInput_.getValue() ? this.installationLocationMasterInput_.clear() :
      constantsEnum.EMPTY_STRING);
    (!this.deliveryDestinationMaster_.getValue() ? this.deliveryDestinationMaster_.clear() : constantsEnum.EMPTY_STRING);
    (!this.acceptingDepartmentMasterInput_.getValue() ? this.acceptingDepartmentMasterInput_.clear() :
      constantsEnum.EMPTY_STRING);
    (!this.receivingPersonMasterInput_.getValue() ? this.receivingPersonMasterInput_.clear() : constantsEnum.EMPTY_STRING);
    (!this.packingMasterInput_.getValue() ? this.packingMasterInput_.clear() : constantsEnum.EMPTY_STRING);
    (!this.deliveryConditionMasterInput_.getValue() ? this.deliveryConditionMasterInput_.clear() : constantsEnum.EMPTY_STRING);
    (!this.orderResponsibleDepartmentMasterInput_.getValue() ? this.orderResponsibleDepartmentMasterInput_.clear() :
      constantsEnum.EMPTY_STRING);
    (!this.transferSourceResponsibleDepartmentMasterInput_.getValue() ? this.transferSourceResponsibleDepartmentMasterInput_
      .clear() : constantsEnum.EMPTY_STRING);
    (!this.vendorMasterInput_.getValue() ? this.vendorMasterInput_.clear() : constantsEnum.EMPTY_STRING);
    (!this.paymentTermsMasterInput_.getValue() ? this.paymentTermsMasterInput_.clear() : constantsEnum.EMPTY_STRING);
    (!this.prepaidExpensesTypeMasterInput_.getValue() ? this.prepaidExpensesTypeMasterInput_.clear() :
      constantsEnum.EMPTY_STRING);
    (!this.inspectionDepartmentMasterInput_.getValue() ? this.inspectionDepartmentMasterInput_.clear() :
      constantsEnum.EMPTY_STRING);
    (!this.acceptanceDepartmentMasterInput_.getValue() ? this.acceptanceDepartmentMasterInput_.clear() :
      constantsEnum.EMPTY_STRING);
    (!this.burdenDepartmentMasterInput_.getValue() ? this.burdenDepartmentMasterInput_.clear() : constantsEnum.EMPTY_STRING);
    (!this.budgetInformationMasterInput_.getValue() ? this.budgetInformationMasterInput_.clear() : constantsEnum.EMPTY_STRING);
    (!this.costClassificationMasterInput_.getValue() ? this.costClassificationMasterInput_.clear() :
      constantsEnum.EMPTY_STRING);
    (!this.assetClassificationMasterInput_.getValue() ? this.assetClassificationMasterInput_.clear() :
      constantsEnum.EMPTY_STRING);
    (!this.transferringPersonInChargeMasterInput_.getValue() ? this.transferringPersonInChargeMasterInput_.clear() :
      constantsEnum.EMPTY_STRING);
    (!this.sendingMethodMasterInput.getValue() ? this.sendingMethodMasterInput.clear() : constantsEnum.EMPTY_STRING);

  };
  /**
   * updateGridData_ method used to update the value.
   * columns in the grid
   * @private
   *   @param {Object} event
   */
  purchaseOrderApplicationInput.prototype.updateGridData_ = function(event) {
    var component = this.orderApplicationLineDetailsSimaGrid_;
    var attachmentMap = {};
    var object = event['data']['cells'];
    var row = object[0]['row'];
    var column = object[0]['column'];
    var data = '';
    var paramData;
    var gridComponent = component.getRow(row - 1);
    var idEnums = purchaseOrderApplicationInput.ID_ENUM_;
    if (gridComponent[this.constants_.ITEM_NAME_VALUE]) {
      var itemName = gridComponent[this.constants_.ITEM_NAME_VALUE];
      attachmentMap['itemName'] = itemName;
      purchaseOrderApplicationInputDao.callAjax_(attachmentMap, 'getDetails')
        .getResult().then(goog.bind(function(response) {
          paramData = response.getResponseJson();

          if (paramData['tagsInput'] === 'false') {}
        }, this));
    } else {
      if (data) {
        component.updateCellAt(row - 1, this.idEnum_.ITEM_NAME_SUPPLEMENT_SIMA_GRID_COLUMN, data);
        component.updateCellAt(row - 1, this.idEnum_.SPECIFICATION_SIMA_GRID_COLUMN, data);
        component.updateCellAt(row - 1, this.idEnum_.SPECIFICATION_SUPPLEMENT_SIMA_GRID_COLUMN, data);
        component.updateCellAt(row - 1, this.idEnum_.UNIT_SIMA_GRID_COLUMN, data);
        component.updateCellAt(row - 1, this.idEnum_.UNIT_PRICE_SIMA_GRID_COLUMN, data);
        component.updateCellAt(row - 1, this.idEnum_.SALES_TAX_RATE_SIMA_GRID_COLUMN, data);
      }
    }
    return row;
  };


  /**
   * getCurrency_ method used to update currency symbol in the amount
   * columns in the grid
   * @private
   */
  purchaseOrderApplicationInput.prototype.getCurrency_ = function() {
    this.initializeComponents_();
    var baseCurrencyOptions = {
      'currency': this.currencyNameLabel_.getLabel(),
      'currencySign': this.currencySignLabel_.getLabel(),
      'showSeparator': true,
      'show-currency-mark': true

    };
    var editorOptions = {
      'currency': this.currencyNameLabel_.getLabel(),
      'currencySign': this.currencySignLabel_.getLabel(),
      'showSeparator': true,
      'show-currency-mark': true
    };
    if (this.currencyNameLabel_.getLabel() === this.constants_.JPY) {
      editorOptions['precision'] = this.constants_.NUMERIC_ZERO;
    } else {
      editorOptions['precision'] = this.constants_.NUMERIC_TWO;
    }
    var amountColumnOptions = {
      'cell:formatter': 'Currency',
      'cell:formatterOptions': baseCurrencyOptions,
      'cell:editor': 'wap.core.ui.zhuge.editor.MoneyEditor',
      'cell:editorOptions': editorOptions
    };

    var options = {};
    options['currencyCode'] = this.currencyNameLabel_.getLabel();
    if (this.currencyNameLabel_.getLabel() === this.constants_.JPY) {
      options['precision'] = this.constants_.NUMERIC_ZERO;
    } else {
      options['precision'] = this.constants_.NUMERIC_TWO;
    }
    this.totalSaleAmountMoneyInput_.overwriteOptions(options);
    this.consumptionTaxAmount_.overwriteOptions(options);
    this.taxIncludedAmount_.overwriteOptions(options);
    this.taxWithdrawalAmount_.overwriteOptions(options);
    this.prepaymentAmountMoneyInput_.overwriteOptions(options);

    if (this.quotationReplyAmountMoneyInput_) {
      this.quotationReplyAmountMoneyInput_.overwriteOptions(options);
    }

    if (this.estimateNotSelectDetailsSimaGrid_) {
      this.estimateNotSelectDetailsSimaGrid_.updateColumn(this.idEnum_.QUOTATION_REPLY_AMOUNT_SIMA_GRID_COLUMN,
        undefined,
        amountColumnOptions, false);
    }

    this.orderApplicationLineDetailsSimaGrid_.updateColumn(this.idEnum_.UNIT_PRICE_SIMA_GRID_COLUMN,
      undefined,
      amountColumnOptions, false);
    this.orderApplicationLineDetailsSimaGrid_.updateColumn(this.idEnum_.CONSUMPTION_TAX_SIMA_GRID_COLUMN,
      undefined,
      amountColumnOptions, false);
    this.orderApplicationLineDetailsSimaGrid_.updateColumn(this.idEnum_.TAX_INCLUDED_AMOUNT_SIMA_GRID_COLUMN,
      undefined,
      amountColumnOptions, false);
    this.orderApplicationLineDetailsSimaGrid_.updateColumn(this.idEnum_.TAX_EXCLUDED_AMOUNT_SIMA_GRID_COLUMN,
      undefined,
      amountColumnOptions, false);

  };

  /**
   * eventHandlers
   *
   * @private
   */
  purchaseOrderApplicationInput.prototype.eventHandlers_ = function() {
    this.logger_.debug('eventHandlers methods starts');
    var eventEnum = purchaseOrderApplicationInput.EVENT_ENUM_;
    if (this.uploaderFromPcHueDriveFileUpload_) {
      var eventEnum = purchaseOrderApplicationInput.EVENT_ENUM_;
      this.getHandler().listen(this.uploaderFromPcHueDriveFileUpload_,
        eventEnum.FILE_UPLOADED,
        function(event) {
          purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
          var param;
          param = this.buttonClicked_;
          this.getUploadedDocument_(event, param);
        });
    }
    if (this.mailAttachmentHueDriveFileSelector_) {

      var eventEnum = purchaseOrderApplicationInput.EVENT_ENUM_;
      this.getHandler().listen(this.mailAttachmentHueDriveFileSelector_,
        eventEnum.FILE_UPLODER_SPLIT,
        function(event) {
          purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
          var param;
          param = this.buttonClicked_;
          this.getUploaded_(event, param);
        });
    }
    if (this.sharedAttachmentHueDriveFileSelector_) {

      var eventEnum = purchaseOrderApplicationInput.EVENT_ENUM_;
      this.getHandler().listen(this.sharedAttachmentHueDriveFileSelector_,
        this.eventEnum_.FILE_UPLODER_SPLIT,
        function(event) {
          purchaseOrderApplicationInput.CONSTANTS_.VALID_FLAG = true;
          var param;
          param = this.buttonClicked_;
          this.getUploaded_(event, param);
        });
    }
    this.getHandler().listen(this.constructionAttachmentSplitButton_,
      eventEnum.SPLITER_ACTION,
      function(event) {
        this.buttonAction(event);

      });

    this.getHandler().listen(this.quotationOverviewSplitButton_,
      eventEnum.SPLITER_ACTION,
      function(event) {
        this.buttonAction(event);

      });


    this.listen_(this.lineEraseButton_,
      eventEnum.BUTTON_ACTION, this.clear_);
    this.logger_.debug('eventHandlers methods ends');

    this.logger_.debug('eventHandlers methods ends');

  };

  /**
   * getComponent_ method for getting the component for the given argument
   * from the view instance.
   *
   * @private
   * @param {String}
   *            componentString
   * @return {Object} componentObject
   */

  purchaseOrderApplicationInput.prototype.getComponent_ = function(componentString) {
    this.logger_.debug('getComponent methods starts');
    var componentObject = this.viewInstance_
      .getComponent(componentString);
    return componentObject;
    this.logger_.debug('getComponent methods ends');

  };



  /**
   * listen_ method
   * partial update method for updating partial update values and performing after function of partial updating.
   *
   * @private
   * @param {Object} element
   * @param {String} event
   * @param {Object} functionValue
   */
  purchaseOrderApplicationInput.prototype.listen_ = function(element, event, functionValue) {
    this.logger_.debug('listen methods starts');
    var handler_ = this.getHandler();
    handler_.listen(element, event, functionValue);
    this.logger_.debug('listen methods ends');
  };

  /**
   * backToPortal_ is used to view the portal page.
   *
   * @private
   * @param {String} event
   */
  purchaseOrderApplicationInput.prototype.backToPortal_ = function(event) {
    this.logger_.debug('backToPortal methods starts');

    var urlValue = wap.core.common.getContextPath() +
      this.constants_.PORTAL_URL;
    goog.window.open(urlValue, {
      'target': '_self'
    });
    this.logger_.debug('backToPortal methods ends');
  };

  /**
   * contractOrderApplicationDetailsGrid_ is used to view the grid in full page.
   *
   * @private
   */
  purchaseOrderApplicationInput.prototype.contractOrderApplicationDetailsGrid_ = function() {

    this.logger_.debug('contractOrderApplicationDetailsGrid methods starts');
    var rowCount;
    var componentObjects = {
      'expandButton': this.lineExpandButton_,
      'contractButton': this.lineContractButton_,
      'gridComp': this.orderApplicationLineDetailsSimaGrid_
    };
    this.commonObject_.contractGrid(componentObjects, this.constants_.INPUT_FORM);
    rowCount = this.orderApplicationLineDetailsSimaGrid_.getSheet().getActiveCell();
    if (rowCount) {
      this.orderApplicationLineDetailsSimaGrid_.getSheet().setActiveCell(rowCount['row'], rowCount['column']);
    } else {
      this.orderApplicationLineDetailsSimaGrid_.getSheet().setActiveCell(1, 1);
    }
    this.logger_.debug('contractOrderApplicationDetailsGrid methods ends');
  };


  /**
   * expandAcquisitionGrid_ expands the Grid into full screen view.
   *
   * @private
   */
  purchaseOrderApplicationInput.prototype.expandOrderApplicationDetailsGrid_ = function() {
    this.logger_.debug('expandOrderApplicationDetailsGrid methods starts');
    var targetIds = [this.idEnum_.MESSAGE_ROW, this.idEnum_.LINE_ITEM_GRID_ROW];
    var componentObjects = {
      'expandButton': this.lineExpandButton_,
      'contractButton': this.lineContractButton_,
      'gridComp': this.orderApplicationLineDetailsSimaGrid_
    };

    this.commonObject_.expandGrid(targetIds, componentObjects, this.constants_.INPUT_FORM);
    var grid = this.viewInstance_.getComponent('order-application-line-details-sima-grid');
    this.logger_.debug('expandOrderApplicationDetailsGrid methods ends');
  };

  /**
   * openPaymentDetails_ is to expand the payment details option
   *
   * @private
   *
   *
   *
   */
  purchaseOrderApplicationInput.prototype.openPaymentDetails_ = function() {
    this.logger_.debug('openPaymentDetails methods starts');
    var textResource = wap.core.txt.TextResource;
    var button = this.getComponent_(this.idEnum_.ENTER_DETAIL_INFORMATION_PAYMENT_ANCHOR_LABEL);
    var label = this.getComponent_(this.idEnum_.ENTER_DETAIL_INFORMATION_PAYMENT_ANCHOR_LABEL)
      .getLabel();

    var textIds = [this.constants_.ENTER_DETAILED_INFORMATION_ON_PAYMENT,
      this.constants_.CLOSE_PREPAYMENT_DETAILS
    ];

    wap.core.txt.TextResource.getTexts(textIds).then(
      goog.bind(function(designFormContent) {
        if (label === designFormContent[this.constants_.ENTER_DETAILED_INFORMATION_ON_PAYMENT]) {
          button.setLabel(designFormContent[this.constants_.CLOSE_PREPAYMENT_DETAILS]);

          var $openPrepayment = this.prepaymentTermsRow_.getElement();
          wap.core.util.style.setElementShown($openPrepayment, true);

          var $openPrepaidExpenseElement = this.prepaidExpensesTypeRow_.getElement();
          wap.core.util.style.setElementShown($openPrepaidExpenseElement, true);



        } else {
          button.setLabel(designFormContent[this.constants_.ENTER_DETAILED_INFORMATION_ON_PAYMENT]);


          var $closePrepayment = this.prepaymentTermsRow_.getElement();
          wap.core.util.style.setElementShown($closePrepayment, false);

          var $closePrepaidExpenseElement = this.prepaidExpensesTypeRow_.getElement();
          wap.core.util.style.setElementShown($closePrepaidExpenseElement, false);

        }
      }, this));
    this.logger_.debug('openPaymentDetails methods ends');
  };

  /**
   * buttonAction is to used to attach a file
   *
   * @param {String} event
   *
   */
  purchaseOrderApplicationInput.prototype.buttonAction = function(
    event) {

    this.logger_.debug('buttonAction methods starts');
    this.buttonClicked_ = event.data[this.constants_.NAME];
    if (event.data[this.constants_.VALUE] === this.constants_.ATTACHED_FROM_PC) {
      this.uploaderFromPcHueDriveFileUpload_.showFileSelectDialog();
    } else if (event.data[this.constants_.VALUE] === this.constants_.MAIL_ATTACHED) {
      this.mailAttachmentHueDriveFileSelector_.setDisplayed(true);
    } else {
      this.sharedAttachmentHueDriveFileSelector_.setDisplayed(true);
    }
    this.logger_.debug('buttonAction methods ends');
  };

  /**
   * getUploadedDocument_ is to used to handle the file upload action.
   *
   * @private
   * @param {String} event
   * @param {String} param
   */
  purchaseOrderApplicationInput.prototype.getUploadedDocument_ = function(
    event, param) {
    this.logger_.debug('getUploadedDocument methods starts');
    var constantEnum = purchaseOrderApplicationInput.CONSTANTS_;
    var internalDocumentsList = [];
    var sessionMap = {};
    var getRequest;
    var attachment = {
      'id': event.uploadData[constantEnum.USER_FILE_ID][constantEnum.FILE_ID],
      'fileName': event.uploadData[constantEnum.FILE_NAME],
      'fileSize': event.uploadData[constantEnum.FILE_SIZE],
      'userId': event.uploadData[constantEnum.USER_FILE_ID][constantEnum.USER_ID],
      'timeUUID': event.uploadData[constantEnum.USER_FILE_ID][constantEnum.TIME_UUID],
      'fileId': event.uploadData[constantEnum.USER_FILE_ID][constantEnum.FILE_ID],
      'param': param
    };
    internalDocumentsList.push(attachment);

    var fileSizeValue;
    var attachmentFileSize = attachment['fileSize'];
    if (attachmentFileSize < 1025) {
      fileSizeValue = this.constants_.ONEKB;
    } else if ((attachmentFileSize / this.constants_.TOTAL_BYTES) < this.constants_.TOTAL_BYTES) {
      fileSizeValue = Math.ceil((attachmentFileSize) / this.constants_.TOTAL_BYTES) + this.constants_.KB;
    } else if (attachmentFileSize / (this.constants_.TOTAL_BYTES * this.constants_.TOTAL_BYTES) < this.constants_
      .TOTAL_BYTES) {
      fileSizeValue = Math.ceil(attachmentFileSize / (this.constants_.TOTAL_BYTES * this.constants_.TOTAL_BYTES)) +
        this.constants_.MB;
    }
    if (attachment['param'] === 'constructionAttachmentSplitButton') {

      this.inhousefileTags_.addTag(attachment['fileName']);
    } else {
      this.fileTags_.addTag(attachment['fileName']);
    }
    sessionMap[constantEnum.INTERNAL_DOCUMENTS_LIST] = internalDocumentsList;
    purchaseOrderApplicationInputDao.callAjax_(sessionMap, constantEnum.SET_EVIDENCE_DETAILS).getResult()
      .then(goog.bind(function(event) {
        var paramData = JSON.parse(event.responseText_);
      }, this));
    this.logger_.debug('getUploadedDocument methods ends');
  };


  /**
   * getUploaded_ is to used to handle the file upload action for another button.
   *
   * @private
   * @param {String} event
   * @param {String} param
   *
   */
  purchaseOrderApplicationInput.prototype.getUploaded_ = function(
    event, param) {
    this.logger_.debug('getUploaded_ methods starts');
    var constantEnum = purchaseOrderApplicationInput.CONSTANTS_;
    var internalDocumentsList = [];
    var attachmentMap = {};
    var getRequestValue;

    goog.array.forEach(event.selectedItems, goog.bind(function(value, index) {
      var attachment = {
        'id': value[constantEnum.FILE_ID],
        'fileName': value[constantEnum.FILE_NAME],
        'fileSize': value[constantEnum.FILE_SIZE],
        'userId': value[constantEnum.ID],
        'timeUUID': value[constantEnum.TIME_UUID],
        'fileId': value[constantEnum.FILE_ID],
        'param': param
      };
      this.fileUpload(attachment);
      internalDocumentsList.push(attachment);
    }, this));

    attachmentMap['internalDocumentsList'] = internalDocumentsList;


    purchaseOrderApplicationInputDao.callAjax_(attachmentMap, constantEnum.SET_EVIDENCE_DETAILS)
      .getResult().then(goog.bind(function(event) {
        var paramData = JSON.parse(event.responseText_);
      }, this));
    this.logger_.debug('getUploadedDocument methods ends');
  };

  /**
   * fileUpload Function is used to get the list of attached hue files.
   *
   * @param {Object} attachment
   * @override
   */
  purchaseOrderApplicationInput.prototype.fileUpload = function(attachment) {
    var fileSizeValue;
    var attachmentFileSize = attachment['fileSize'];
    if (attachmentFileSize < 1025) {
      fileSizeValue = this.constants_.ONEKB;
    } else if ((attachmentFileSize / this.constants_.TOTAL_BYTES) < this.constants_.TOTAL_BYTES) {
      fileSizeValue = Math.ceil((attachmentFileSize) / this.constants_.TOTAL_BYTES) + this.constants_.KB;
    } else if (attachmentFileSize / (this.constants_.TOTAL_BYTES * this.constants_.TOTAL_BYTES) < this.constants_
      .TOTAL_BYTES) {
      fileSizeValue = Math.ceil(attachmentFileSize / (this.constants_.TOTAL_BYTES * this.constants_.TOTAL_BYTES)) +
        this.constants_.MB;
    }
    if (attachment['param'] === 'construction-mail-dropdown-item' || attachment['param'] ===
      'construction-shared-dropdown-item') {

      this.inhousefileTags_.addTag(attachment['fileName']);
    } else {
      this.fileTags_.addTag(attachment['fileName']);
    }

  };

  /**
   * insertGridData_ method is used to reinsert the values of auto completing fields input in sima grid
   * @private
   *
   *
   */
  purchaseOrderApplicationInput.prototype.insertGridData_ = function() {
    this.logger_.debug('insertGridData_ method starts');
    var sheet = this.orderApplicationLineDetailsSimaGrid_.getSheet();
    var rowCount = this.orderApplicationLineDetailsSimaGrid_.getRowsCount();
    var columns = this.orderApplicationLineDetailsSimaGrid_.getColumns();
    var columnCount = this.orderApplicationLineDetailsSimaGrid_.getColumnsCount();
    for (var fromIndex = this.constants_.NUMERIC_ONE; fromIndex < rowCount + this.constants_.NUMERIC_ONE; fromIndex++) {
      var gridComponent = this.orderApplicationLineDetailsSimaGrid_.getRow(fromIndex - this.constants_.NUMERIC_ONE);
      var data;
      if (gridComponent[this.constants_.ITEM_NAME_VALUE]) {
        data = {};
        for (var iterator = this.constants_.NUMERIC_ONE; iterator < columnCount; iterator++) {
          data[columns[iterator].getField()] = this.orderApplicationLineDetailsSimaGrid_.getSheet().getCellValue(
            fromIndex, iterator);
        }
        this.orderApplicationLineDetailsSimaGrid_.updateCellAt(fromIndex - this.constants_.NUMERIC_ONE,
          this.idEnum_.ITEM_NAME_SUPPLEMENT_SIMA_GRID_COLUMN, data.itemNameSupplementValue, true, false);
        this.orderApplicationLineDetailsSimaGrid_.updateCellAt(fromIndex - this.constants_.NUMERIC_ONE,
          this.idEnum_.SPECIFICATION_SIMA_GRID_COLUMN, data.specificationValue, true, false);
        this.orderApplicationLineDetailsSimaGrid_.updateCellAt(fromIndex - this.constants_.NUMERIC_ONE,
          this.idEnum_.SPECIFICATION_SUPPLEMENT_SIMA_GRID_COLUMN, data.specificationSupplementValue, true,
          false);
        this.orderApplicationLineDetailsSimaGrid_.updateCellAt(fromIndex - this.constants_.NUMERIC_ONE,
          this.idEnum_.UNIT_SIMA_GRID_COLUMN, data.unitNameValue, true, false);
        this.orderApplicationLineDetailsSimaGrid_.updateCellAt(fromIndex - this.constants_.NUMERIC_ONE,
          this.idEnum_.UNIT_PRICE_SIMA_GRID_COLUMN, data.unitPrice, true, false);
        this.orderApplicationLineDetailsSimaGrid_.updateCellAt(fromIndex - this.constants_.NUMERIC_ONE,
          this.idEnum_.SALES_TAX_RATE_SIMA_GRID_COLUMN, data.consumptionTaxRateValue, true, false);
      }
    }
    this.logger_.debug('insertGridData_ method ends');
  };

  /**
   * updateGridColumns_ method used to update grid column options
   *
   * @private
   */
  purchaseOrderApplicationInput.prototype.updateGridColumns_ = function() {
    this.logger_.debug('updateGridColumns_ method starts');
    this.updateColumnMapping_(this.orderApplicationLineDetailsSimaGrid_);
    this.logger_.debug('updateGridColumns_ method ends');
  };

  /**
   * updateColumnMapping_ method used to update column time in grid.
   *
   * @private
   *
   * @param {wap.core.ui.sima.Grid} grid
   */
  purchaseOrderApplicationInput.prototype.updateColumnMapping_ = function(grid) {
    this.logger_.debug('updateColumnMapping_ method starts');
    var mappingColumnOptions = {};
    var possibleMappingColumnOptions = {};
    var includedMappingColumnOptions = {};
    var taxClassificationMappingColumnOptions = {};
    var constantEnum = purchaseOrderApplicationInput.CONSTANTS_;
    var eventEnum = purchaseOrderApplicationInput.EVENT_ENUM_;
    var idEnum = purchaseOrderApplicationInput.ID_ENUM_;
    var textIdList = [constantEnum.PC_POMT_NONEEDS, constantEnum.PC_POMT_NEEDSACTUALMEASUREMENT,
      constantEnum.PC_POMT_POSSIBLE, constantEnum.PC_POMT_IMPOSSIBLE,
      constantEnum.PC_POMT_EXCLUDED, constantEnum.PC_POMT_INCLUDED,
      constantEnum.PC_POMT_EXCLUDINGTAX, constantEnum.PC_POMT_INCLUDINGTAX, constantEnum.PC_POMT_TAXEXEMPT
    ];
    var params = {};
    wap.core.txt.TextResource.getTexts(textIdList).then(function(texts) {
      mappingColumnOptions = {
        'cell:formatter': 'Mapping',
        'cell:formatterOptions': {
          '0': texts[constantEnum.PC_POMT_NEEDSACTUALMEASUREMENT],
          '1': texts[constantEnum.PC_POMT_NONEEDS]

        },
        'cell:editor': eventEnum.SINGLE_SELECT,
        'cell:editorOptions': {
          'name': eventEnum.SINGLE_SELECT,
          'options': [{
            'id': '',
            'divider-class': constantEnum.EMPTY_STRING,
            'value': constantEnum.ZERO_VALUE,
            'label': texts[constantEnum.PC_POMT_NEEDSACTUALMEASUREMENT]
          }, {
            'id': '',
            'divider-class': constantEnum.EMPTY_STRING,
            'value': constantEnum.ONE_VALUE,
            'label': texts[constantEnum.PC_POMT_NONEEDS]
          }]
        }
      };

      possibleMappingColumnOptions = {
        'cell:formatter': 'Mapping',
        'cell:formatterOptions': {
          '0': texts[constantEnum.PC_POMT_POSSIBLE],
          '1': texts[constantEnum.PC_POMT_IMPOSSIBLE]

        },
        'cell:editor': eventEnum.SINGLE_SELECT,
        'cell:editorOptions': {
          'name': eventEnum.SINGLE_SELECT,

          'options': [{
            'id': constantEnum.EMPTY_STRING,
            'divider-class': constantEnum.EMPTY_STRING,
            'value': constantEnum.ZERO_VALUE,
            'label': texts[constantEnum.PC_POMT_POSSIBLE]
          }, {
            'id': constantEnum.EMPTY_STRING,
            'divider-class': constantEnum.EMPTY_STRING,
            'value': constantEnum.ONE_VALUE,
            'label': texts[constantEnum.PC_POMT_IMPOSSIBLE]
          }]
        }
      };

      includedMappingColumnOptions = {
        'cell:formatter': 'Mapping',
        'cell:formatterOptions': {
          '0': texts[constantEnum.PC_POMT_EXCLUDED],
          '1': texts[constantEnum.PC_POMT_INCLUDED]

        },
        'cell:editor': eventEnum.SINGLE_SELECT,
        'cell:editorOptions': {
          'name': eventEnum.SINGLE_SELECT,

          'options': [{
            'id': constantEnum.EMPTY_STRING,
            'divider-class': constantEnum.EMPTY_STRING,
            'value': constantEnum.ZERO_VALUE,
            'label': texts[constantEnum.PC_POMT_EXCLUDED]
          }, {
            'id': constantEnum.EMPTY_STRING,
            'divider-class': constantEnum.EMPTY_STRING,
            'value': constantEnum.ONE_VALUE,
            'label': texts[constantEnum.PC_POMT_INCLUDED]
          }]
        }
      };

      taxClassificationMappingColumnOptions = {
        'cell:formatter': 'Mapping',
        'cell:formatterOptions': {
          'EXCLUDING_TAX': texts[constantEnum.PC_POMT_EXCLUDINGTAX],
          'INCLUDING_TAX': texts[constantEnum.PC_POMT_INCLUDINGTAX],
          'TAX_EXEMPT': texts[constantEnum.PC_POMT_TAXEXEMPT]

        },
        'cell:editor': eventEnum.SINGLE_SELECT,
        'cell:editorOptions': {
          'name': eventEnum.SINGLE_SELECT,

          'options': [{
            'id': constantEnum.EMPTY_STRING,
            'divider-class': constantEnum.EMPTY_STRING,
            'value': constantEnum.EXCLUDING_TAX,
            'label': texts[constantEnum.PC_POMT_EXCLUDINGTAX]
          }, {
            'id': constantEnum.EMPTY_STRING,
            'divider-class': constantEnum.EMPTY_STRING,
            'value': constantEnum.INCLUDING_TAX,
            'label': texts[constantEnum.PC_POMT_INCLUDINGTAX]
          }, {
            'id': constantEnum.EMPTY_STRING,
            'divider-class': constantEnum.EMPTY_STRING,
            'value': constantEnum.TAX_EXEMPT,
            'label': texts[constantEnum.PC_POMT_TAXEXEMPT]
          }]
        }
      };
      grid.updateColumn(idEnum.ACTUAL_CLASSIFICATION_SIMA_GRID_COLUMN, undefined, mappingColumnOptions,
        false);
      grid.updateColumn(idEnum.POSSIBILITY_OF_REPLACEMENT_SIMA_GRID_COLUMN, undefined,
        possibleMappingColumnOptions, false);
      grid.updateColumn(idEnum.SCHEDULED_DELIVERY_SIMA_GRID_COLUMN, undefined,
        possibleMappingColumnOptions,
        false);
      grid.updateColumn(idEnum.SUBCONTRACT_COVERAGE_SIMA_GRID_COLUMN, undefined,
        includedMappingColumnOptions,
        false);
      grid.updateColumn(idEnum.TAX_CLASSIFICATION_SIMA_GRID_COLUMN, undefined,
        taxClassificationMappingColumnOptions,
        false);
      grid.updateColumn(idEnum.CONSUMPTION_TAX_DISTINCTION_SIMA_GRID_COLUMN, undefined,
        taxClassificationMappingColumnOptions,
        false);
    });
    this.logger_.debug('updateColumnMapping_ method ends');
  };

  /**
   * clear_ is  used to clear the row in grid
   *
   * @private
   */
  purchaseOrderApplicationInput.prototype.clear_ = function() {
    this.logger_.debug('clear_  method starts');
    this.totalSaleAmountMoneyInput_.clear();
    this.taxIncludedAmount_.clear();
    this.consumptionTaxAmount_.clear();
    this.taxWithdrawalAmount_.clear();
    this.orderApplicationLineDetailsSimaGrid_.deleteAllItems();
    this.totalSaleAmountMoneyInput_.setValue(0);
    this.orderApplicationLineDetailsSimaGrid_.appendRows([{}]);
    this.orderApplicationLineDetailsSimaGrid_.deleteRows(this.orderApplicationLineDetailsSimaGrid_.getRowsCount() -
      this.constants_.NUMERIC_ONE);
    this.logger_.debug('clear_  method ends');
  };

  /**
   * getText_ gets the label based on the locale.
   *
   * @private
   * @param {string} textId
   * @param { string} params
   * @return {String} textId
   * @return {String} params
   */
  purchaseOrderApplicationInput.prototype.getText_ = function(textId, params) {
    return wap.core.txt.TextResource.getText(textId, params);
  };

  /**
   * @override
   * preFormChanged used for message panel to remmove previous messages
   * @param {Object} event
   */
  purchaseOrderApplicationInput.prototype.preFormChanged = function(event) {
    this.refreshFormModelHelper();
    var helper = this.getFormModelHelper();
  };

  /**
   * setEmptyMasterInputSubItemValue_ used to clear Master Input Values.
   *
   * @private
   *
   */
  purchaseOrderApplicationInput.prototype.setEmptyMasterInputSubItemValue_ = function() {
    var componentInitialize = purchaseOrderApplicationInput.ID_ENUM_;
    var masterInputIdList = [componentInitialize.VENDOR_MASTER_INPUT];
    goog.array.forEach(masterInputIdList, function(masterInputId, index) {
      var masterInputSubItemId = masterInputId + '-subitem';
      var masterInputSubItemComponent = this.getComponent_(masterInputSubItemId);
      if (masterInputSubItemComponent) {
        if (masterInputSubItemComponent.getValue() === '') {
          masterInputSubItemComponent.setValue({});
        }
      }
    }, this);
  };
  /**
   * clearMLStringMasterInput_ used to clear MLString Master Input Values.
   *
   * @private
   *
   */
  purchaseOrderApplicationInput.prototype.clearMLStringMasterInput_ = function() {
    var componentInitialize = purchaseOrderApplicationInput.ID_ENUM_;
    var masterInputIdList = [componentInitialize.VENDOR_MASTER_INPUT];
    var eventEnum = purchaseOrderApplicationInput.EVENT_ENUM_;
    goog.array.forEach(masterInputIdList, function(masterInputId, index) {
      var masterInputSubItemId = masterInputId + '-subitem';
      var masterInputComponent = this.getComponent_(masterInputId);
      if (masterInputComponent) {
        var masterInputSubItemComponent = this.getComponent_(masterInputSubItemId);
        this.listen_(masterInputComponent, eventEnum.MASTER_ACTION, function(event) {
          if (masterInputComponent.getValue() === purchaseOrderApplicationInput.CONSTANTS_.EMPTY_STRING) {
            masterInputComponent.clear();
            masterInputSubItemComponent.setValue({});
          }
        });
      }
    }, this);
  };

  /**
   * setMasterInputSubItemValue_ used to set Master Input SubIem Values.
   *
   * @private
   *
   */
  purchaseOrderApplicationInput.prototype.setMasterInputSubItemValue_ = function() {
    var componentInitialize = purchaseOrderApplicationInput.ID_ENUM_;
    var masterInputIdList = [componentInitialize.VENDOR_MASTER_INPUT];
    goog.array.forEach(masterInputIdList, function(masterInputId, index) {
      var masterInputSubItemId = masterInputId + '-subitem';
      var masterInputComponent = this.getComponent_(masterInputId);
      if (masterInputComponent) {
        var masterInputSubItemComponent = this.getComponent_(masterInputSubItemId);
        var masterInputNameValue = masterInputComponent.getShowText();
        var mlStringNameValue = wap.core.txt.MLStringUtils.getMLString(goog.locale.getLocale(),
          masterInputNameValue);
        masterInputSubItemComponent.setValue(mlStringNameValue);
      }
    }, this);
  };

});