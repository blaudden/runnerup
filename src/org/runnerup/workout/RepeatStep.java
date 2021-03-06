/*
 * Copyright (C) 2013 jonas.oreland@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.runnerup.workout;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Pair;

public class RepeatStep extends Step {

	int repeatCount = 1;
	ArrayList<Step> steps = new ArrayList<Step>();

	int currentStep = 0;
	int currentRepeat = 0;

	public RepeatStep() {
		this.intensity = Intensity.REPEAT;
		this.durationType = null;
		this.targetType = null;
	}
	
	@Override
	public Dimension getDurationType() {
		return null;
	}

	@Override
	public void onInit(Workout w, HashMap<String, Object> bindValues) {
		currentStep = 0;
		currentRepeat = 0;
		for (Step s : steps) {
			s.onInit(w, bindValues);
		}
	}

	@Override
	public void onEnd(Workout w) {
		currentStep = 0;
		currentRepeat = 0;
		for (Step s : steps) {
			s.onEnd(w);
		}
	}

	@Override
	public void onStart(Scope what, Workout s) {
		steps.get(currentStep).onStart(what, s);
	}

	@Override
	public void onStop(Workout s) {
		steps.get(currentStep).onStop(s);
	}

	@Override
	public void onPause(Workout s) {
		steps.get(currentStep).onPause(s);
	}

	@Override
	public boolean onTick(Workout w) {
		if (steps.get(currentStep).onTick(w)) {
			steps.get(currentStep).onComplete(Scope.LAP, w);
			steps.get(currentStep).onComplete(Scope.STEP, w);

			return onNextStep(w);
		}
		return false;
	}

	@Override
	public boolean onNextStep(Workout w) {
		if (steps.get(currentStep).onNextStep(w)) {
			currentStep++;
			if (currentStep == steps.size()) {
				currentRepeat ++;
				if (currentRepeat == repeatCount) {
					// reset in preparation for next outer iteration
					currentStep = 0;
					currentRepeat = 0;
					return true;
				}
				currentStep = 0;
			}
			steps.get(currentStep).onStart(Scope.STEP, w);
			steps.get(currentStep).onStart(Scope.LAP, w);
		}
		return false;
	}
	
	@Override
	public void onResume(Workout s) {
		steps.get(currentStep).onPause(s);
	}

	@Override
	public void onComplete(Scope scope, Workout s) {
		steps.get(currentStep).onComplete(scope, s);
	}

	@Override
	public long getDistance(Workout w, Scope s) {
		return steps.get(currentStep).getDistance(w, s);
	}

	@Override
	public long getTime(Workout w, Scope s) {
		return steps.get(currentStep).getTime(w, s);
	}

	@Override
	public double getSpeed(Workout w, Scope s) {
		return steps.get(currentStep).getSpeed(w, s);
	}

	@Override
	public double getDuration(Dimension dimension) {
		return steps.get(currentStep).getDuration(dimension);
	}

	@Override
	public int getRepeatCount() {
		return repeatCount;
	}
	
	@Override
	public int getCurrentRepeat() {
		return currentRepeat;
	}

	@Override
	public Step getCurrentStep() {
		if (currentStep < steps.size())
			return steps.get(currentStep);
		return null;
	}

	@Override
	public void getSteps(Step parent, ArrayList<Pair<Step, Step>> list) {
		list.add(Pair.create(parent, (Step)this));
		for (Step s2 : steps) {
			s2.getSteps(this, list);
		}
	}

}
