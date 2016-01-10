package com.wendy.quantumwolf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.util.SparseArray;
import android.util.SparseIntArray;

public class GamePlay {
	private int numPlayers;

	private double[] probGood;
	private double[] probDead;
	private double[] probSeer;
	private List<double[]> probDominant;
	private int numWolves;
	private int currentNight = 0;
	private List<Integer> burned = new ArrayList<>();
	private List<Integer> killed = new ArrayList<>();
	private SparseArray<SparseArray<Boolean>> seerMap = new SparseArray<>();
	/**
	 * Map of target to the list of people who have targeted them.
	 */
	private List<SparseIntArray> targetMap = new ArrayList<SparseIntArray>();

	public GamePlay(int numPlayers, int numWolves) {
		this.numPlayers = numPlayers;
		probGood = new double[numPlayers];
		probDead = new double[numPlayers];
		probSeer = new double[numPlayers];
		Arrays.fill(probGood, 1 - numWolves / (numPlayers * 1d));
		Arrays.fill(probSeer, 1d / numPlayers);
		Arrays.fill(probDead, 0);
		this.numWolves = numWolves;
		double[] domArray = new double[numPlayers];
		Arrays.fill(domArray, 1d / numPlayers);
		probDominant = new ArrayList<double[]>();
		probDominant.add(domArray);

	}

	public void burnPerson(int person) {
		burned.add(person);
		for (int i = 0; i < currentNight - 1; i++) {
			SparseIntArray targetsForNight = targetMap.get(i);
			for (int j = 0; j < numPlayers; j++) {
				int theTarget = targetsForNight.get(j, -1);
				if (theTarget == person) {
					for (int k = 0; k <= i; k++)
						probDominant.get(k)[j] = 0;
				}
			}

		}
		personDies(person);
	}

	public List<Integer> getAlivePlayers() {
		List<Integer> alive = new ArrayList<>();
		for (int i = 0; i < numPlayers; i++)
			if (probDead[i] < 1)
				alive.add(i);
		return alive;
	}

	private void personDies(int person) {
		probGood[person] = Math.random() <= probGood[person] ? 1 : 0;
		probDead[person] = 1d;
		if (probGood[person] == 1) {

			// If they are good they can't be the dominant wolf
			for (int i = 0; i < currentNight; i++)
				probDominant.get(i)[person] = 0;

			normalizeGood();
		} else {
			probSeer[person] = 0;
			adjustProbabilities();
		}

	}

	private void normalizeGood() {
		double totalGood = 0;
		int decided = 0;
		for (int i = 0; i < numPlayers; i++) {
			if (probGood[i] < 1 && probGood[i] > 0)
				totalGood += probGood[i];
			else if (probGood[i] == 1)
				decided++;

		}
		for (int i = 0; i < numPlayers; i++) {
			if (probGood[i] < 1 && probGood[i] > 0)
				probGood[i] = probGood[i] / totalGood * (numPlayers - numWolves - decided);
		}
	}

	public void adjustProbabilities() {
		/*
		 * Adjust Seer Probability
		 */
		boolean haveASeer = false;
		int seer = -1;
		for (int i = 0; i < numPlayers; i++) {
			for (int j = 0; j < numPlayers; j++) {
				if ((seerMap.get(i).get(j) && 0 == probGood[j]) || (!seerMap.get(i).get(j) && 1 == probGood[j])) {
					probSeer[j] = 0;
				}
			}
		}

		int countPossible = numPlayers;
		double totalProbGood = 0;
		for (int k = 0; k < numPlayers; k++) {
			if (probSeer[k] == 0)
				countPossible--;
			else {
				totalProbGood += probGood[k];
			}
		}
		haveASeer = countPossible == 1;
		for (int k = 0; k < numPlayers; k++) {
			if (probSeer[k] != 0) {
				if (haveASeer) {
					seer = k;
					probSeer[k] = 1;
				} else
					probSeer[k] = probGood[k] / totalProbGood;
			}

		}

		if (seer > -1) {
			for (int i = 0; i < numPlayers; i++) {
				probGood[i] = seerMap.get(seer).get(i) ? 1 : 0;
				if (probGood[i] == 1)
					for (int j = 0; j < currentNight; j++)
						probDominant.get(j)[i] = 0;
			}
		}

		// Adjust Dominance

		adjustDominance();
	}

	/**
	 * 
	 * @return True if good wins, false if wolves win, null if not over
	 */
	public Boolean isGameOver() {

		int numAlive = 0;
		int goodAlive = 0;
		int badAlive = 0;
		for (int i = 0; i < numPlayers; i++) {
			if (probDead[i] < 1) {
				numAlive++;
				if (probGood[i] == 1)
					goodAlive++;
				if (probGood[i] == 0)
					badAlive++;
			}
		}
		if (numAlive == goodAlive)
			return true;
		if (numAlive == badAlive)
			return false;
		if (numAlive == 2 && goodAlive == 0 && badAlive == 0)
			return Math.random() < 0.5;
		return null;
	}

	private void adjustDominance() {
		for (int i = 0; i < currentNight; i++) {
			// reNormalize
			double totalProb = 0;

			for (int j = 0; j < numPlayers; j++)
				totalProb += probDominant.get(i)[j];
			for (int j = 0; j < numPlayers; j++) {
				probDominant.get(i)[j] /= totalProb;
				if (probDominant.get(i)[j] == 1) {
					// have Dominant
					for (int k = 0; k <= i; k++) {
						if (killed.get(k) == null) {
							int kiilledPerson = targetMap.get(k).get(j);
							killed.set(k, kiilledPerson);
							probGood[kiilledPerson] = 1d;
							personDies(kiilledPerson);
						}
					}
					probGood[j] = 0;
					normalizeGood();
				}
			}
		}
	}

	public void vision(Map<Integer, Integer> visionRequests) {
		for (Integer requestor : visionRequests.keySet()) {
			if (seerMap.get(requestor) == null)
				seerMap.put(requestor, new SparseArray<Boolean>());
			if (seerMap.get(requestor).get(visionRequests.get(requestor)) == null)
				seerMap.get(requestor).put(visionRequests.get(requestor),
						Math.random() < probGood[visionRequests.get(requestor)]);
		}
		adjustProbabilities();
	}

	public void makeTargets(Map<Integer, Integer> targets) {
		currentNight++;
		probDominant.add(probDominant.get(currentNight - 1).clone());
		int totalTargets = 0;
		double[] hitRate = new double[numPlayers];
		targetMap.add(new SparseIntArray());
		for (int i = 0; i < numPlayers; i++) {
			if (targets.get(i) != null) {
				targetMap.get(targetMap.size() - 1).put(i, targets.get(i));
				hitRate[targets.get(i)] += 1 - probGood[i];
				totalTargets++;
			}
		}

		boolean someoneKilled = false;
		for (int i = 0; i < numPlayers; i++) {
			if (probDead[i] == 1)
				continue;
			probDead[i] = probDead[i] + (1 - probDead[i]) * hitRate[i] / totalTargets;
			if (probDead[i] == 1) {
				someoneKilled = true;
				killed.add(i);
				probGood[i] = 1;
				personDies(i);
			}
		}
		if (!someoneKilled)
			killed.add(null);

	}

	public int randomInt(int range) {
		return (int) Math.round(Math.random() * range);
	}

}
