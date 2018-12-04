package RealTime.CheckTrial;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;

public class FindPrivateJs {
	 static List<String> privateFunctionsList=new ArrayList<>();
	public  boolean getPrivatePopover (String filePath) {
		List<String> not = Arrays.asList("setRenderType_", "setAnchorElement_",
				"bindAnchorEvent_", "bindPopoverElementEvent_",
				"bindCloseButtonEvent_", "bindEventsAtDocument_", "keydown_",
				"toggleWithClick_", "leaveAnchorForClose_", "show_",
				"showAgain_", "renderPartialUpdate_", "renderContent_",
				"vnl2br_", "createAnchorLabel_", "showPopover_", "onEnd_",
				"reAllocate_", "getArrowSize_", "reAllocateImpl_",
				"getLeftOfTopAndBottomPosition_",
				"getTopOfLeftAndRightPosition_", "adjustArrowPosition_",
				"updateArrowPlace_", "close_", "isOpened_", "dispatch_"
//				"viewPortManager_",
//				"useOldLogic_",
//				"viewPortManager_",
//				"isHover_",
//				"targetId_",
//				"htmlEscape_",
//				"title_",
//				"message_",
//				"anchorMessage_",
//				"deferredUrl_",
//				"place_",
//				"arrowPosition_",
//				"trigger_",
//				"updateAlways_",
//				"isCloseDocumentClick_",
//				"disable_",
//				"domInside_",
//				"animation_",
//				"isResponsive_",
//				"hasSubtree_",
//				"$anchor_",
//				"$arrow_",
//				"renderType_",
//				"isAlreadyRendered_",
//				"getQueryParam_",
//				"anchorHandler_",
//				"hoverCloseTimerId_",
//				"fadeOut_",
//				"$scrollTarget_",
//				"showing_",
//				"currentPlace_",
//				"isHover_",
//				"useOldLogic_",
//				"viewPortManager_"
				);
		
		List<String> simaGridPopoverPrivate=Arrays.asList("popovers_",
"shownPopovers_",
"isCanvasDragging_",
"options_",
"defaultOptions_",
"defaultOptions_",
"popoverOptions_",
"enabled_",
"options_",
"sheet_",
"isCanvasDragging_",
"popovers_",
"shownPopovers_",
"metaKeys_",
"superClass_",
"options_",
"metaKeys_",
"options_",
"options_",
"metaKeys_",
"options_",
"metaKeys_",
"sheet_",
"enabled_",
"sheet_",
"sheet_",
"sheet_",
"sheet_",
"sheet_",
"sheet_",
"documentClickHandler_",
"documentClickHandler_",
"documentClickHandler_",
"onDocumentClick_",
"enabled_",
"documentClickHandler_",
"documentClickHandler_",
"onDocumentClick_",
"documentClickHandler_",
"enabled_",
"popovers_",
"shownPopovers_",
"sheet_",
"isCanvasDragging_",
"superClass_",
"enabled_",
"shownPopovers_",
"popovers_",
"shownPopovers_",
"delegator_",
"showPopoverAtSheet_",
"delegator_",
"hidePopoverAtSheet_",
"showPopoverAtSheet_",
"showPopoverAtSheet_",
"getPopoverKey_",
"popovers_",
"getCellPopoverData_",
"popovers_",
"shownPopovers_",
"repositionePopover_",
"updatePopover_",
"shownPopovers_",
"createPopover_",
"popovers_",
"repositionePopover_",
"updatePopover_",
"shownPopovers_",
"hidePopoverAtSheet_",
"hidePopoverAtSheet_",
"getPopoverKey_",
"shownPopovers_",
"popovers_",
"shownPopovers_",
"getPopoverKey_",
"shownPopovers_",
"getCellPopoverData_",
"sheet_",
"options_",
"metaKeys_",
"metaKeys_",
"createPopover_",
"getCellPopoverData_",
"popoverOptions_",
"options_",
"createPopoverInstance_",
"getPopoverKey_",
"delegator_",
"createPopoverInstance_",
"repositionePopover_",
"sheet_",
"options_",
"sheet_",
"options_",
"updatePopover_",
"isCanvasDragging_",
"options_",
"sheet_",
"sheet_",
"hidePopoverAtSheet_",
"isCanvasDragging_",
"options_",
"sheet_",
"sheet_",
"showPopoverAtSheet_",
"isCanvasDragging_",
"isCanvasDragging_",
"isCanvasDragging_",
"showPopoverAtSheet_",
"onDocumentClick_",
"sheet_",
"getPopoverKey_",
"shownPopovers_",
"popovers_"
);
		privateFunctionsList.clear();
		File jsFile = new File(filePath);
		CompilerEnvirons env = new CompilerEnvirons();
		env.setRecordingLocalJsDocComments(true);
		env.setRecordingComments(true);
		env.setStrictMode(false);
		env.setAllowMemberExprAsFunctionName(true);
		env.setAllowSharpComments(true);
		env.setGeneratingSource(true);
		env.setIdeMode(true);
		try {
			AstRoot root = new Parser(env).parse(new FileReader(jsFile), null,
					1);
			FunctionCallVisit visitor = new FindPrivateJs().new FunctionCallVisit();
			root.visit(visitor);
			AtomicBoolean flag=new AtomicBoolean(false);
			privateFunctionsList.stream().forEach(privat ->{
				if(simaGridPopoverPrivate.contains(privat)){
					flag.set(true);
				}
			});
			return flag.get();
		} catch (Exception e) {
			return false;
		}

	//	return false;

	}


	private class FunctionCallVisit implements NodeVisitor {
		@Override
		public boolean visit(AstNode node) {
			if (node.getType() == Token.GETPROP) {
				//System.out.println(node.toSource());
				if (node.toSource().endsWith("_")) {
					privateFunctionsList.add(node.toSource().substring(
						node.toSource().lastIndexOf(".") + 1));
//					System.out.println(node.toSource().substring(
//							node.toSource().lastIndexOf(".") + 1));
					//System.out.println("-----------------------");
				}

			}
			return true;
		}
	}
}
