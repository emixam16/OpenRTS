package app;

import model.ModelManager;
import model.editor.ToolManager;
import view.EditorView;
import view.mapDrawing.MapDrawer;

import com.google.common.eventbus.Subscribe;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;

import controller.Controller;
import controller.battlefield.BattlefieldController;
import controller.editor.EditorController;
import controller.ground.GroundController;
import event.EventManager;
import event.ControllerChangeEvent;
import geometry.tools.LogUtil;

public class MainRTS extends OpenRTSApplication {

	EditorView view;
	MapDrawer tr;
	BattlefieldController fieldCtrl;
	EditorController editorCtrl;
	GroundController groundCtrl;
	Controller actualCtrl;

	public static void main(String[] args) {
		OpenRTSApplication.main(new MainRTS());
	}

	@Override
	public void simpleInitApp() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		view = new EditorView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);

		fieldCtrl = new BattlefieldController(view, niftyDisplay.getNifty(), inputManager, cam);
		editorCtrl = new EditorController(view, niftyDisplay.getNifty(), inputManager, cam);
		groundCtrl = new GroundController(view, inputManager, cam);
		EventManager.register(this);

		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		// niftyDisplay.getNifty().validateXml("interface/screen.xml");
		niftyDisplay.getNifty().fromXml("interface/screen.xml", "editor");

		actualCtrl = editorCtrl;
		stateManager.attach(actualCtrl);
		actualCtrl.setEnabled(true);

		guiViewPort.addProcessor(niftyDisplay);

		ModelManager.setNewBattlefield();
	}

	@Override
	public void simpleUpdate(float tpf) {
		float maxedTPF = Math.min(tpf, 0.1f);
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
		view.getActorManager().render();
		actualCtrl.update(maxedTPF);
		ModelManager.updateConfigs();
	}

	@Override
	public void destroy() {
		ToolManager.killSower();
	}

	@Subscribe
	public void handleEvent(ControllerChangeEvent e) {
		Controller desiredCtrl;
		switch (e.getControllerIndex()) {
			case 0:
				desiredCtrl = fieldCtrl;
				break;
			case 1:
				desiredCtrl = editorCtrl;
				break;
			case 2:
				desiredCtrl = groundCtrl;
				break;
			default:
				return;
		}
		LogUtil.logger.info("switching controller to " + desiredCtrl.getClass().getSimpleName());

		stateManager.detach(actualCtrl);
		actualCtrl.setEnabled(false);
		actualCtrl = desiredCtrl;
		stateManager.attach(actualCtrl);
		actualCtrl.setEnabled(true);

	}
}
