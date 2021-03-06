/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ground;

import geometry.tools.LogUtil;
import view.EditorView;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.Controller;
import controller.cameraManagement.GroundCameraManager;

/**
 *
 * @author Benoît
 */
public class GroundController extends Controller {

	public GroundController(EditorView view, InputManager inputManager, Camera cam) {
		super(view, inputManager, cam);

		inputInterpreter = new GroundInputInterpreter(this);
		cameraManager = new GroundCameraManager(cam);
	}


	@Override
	public void update(float elapsedTime) {
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		inputManager.setCursorVisible(false);
		LogUtil.logger.info("ground controller on line");
	}



}
