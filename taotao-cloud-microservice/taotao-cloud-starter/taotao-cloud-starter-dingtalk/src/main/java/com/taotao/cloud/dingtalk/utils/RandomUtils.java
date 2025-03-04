/*
 * Copyright (c) ©2015-2021 Jaemon. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.dingtalk.utils;

import java.util.Random;

/**
 * <p><code>RandomUtils</code> is a wrapper that supports all possible
 * {@link Random} methods via the {@link Math#random()} method and its system-wide
 * <code>Random</code> object.
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:26:40
 */
public class RandomUtils {

	/**
	 * An instance of {@link JVMRandom}.
	 */
	public static final Random JVM_RANDOM = new JVMRandom();

// should be possible for JVM_RANDOM?
//    public static void nextBytes(byte[]) {
//    public synchronized double nextGaussian();
//    }

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed int value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random int
	 */
	public static int nextInt() {
		return nextInt(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed int value
	 * from the given <code>random</code> sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random int
	 */
	public static int nextInt(Random random) {
		return random.nextInt();
	}

	/**
	 * <p>Returns a pseudorandom, uniformly distributed int value
	 * between <code>0</code> (inclusive) and the specified value (exclusive), from the
	 * Math.random() sequence.</p>
	 *
	 * @param n the specified exclusive max-value
	 * @return the random int
	 */
	public static int nextInt(int n) {
		return nextInt(JVM_RANDOM, n);
	}

	/**
	 * <p>Returns a pseudorandom, uniformly distributed int value
	 * between <code>0</code> (inclusive) and the specified value (exclusive), from the given Random
	 * sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @param n      the specified exclusive max-value
	 * @return the random int
	 */
	public static int nextInt(Random random, int n) {
		// check this cannot return 'n'
		return random.nextInt(n);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed long value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random long
	 */
	public static long nextLong() {
		return nextLong(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed long value
	 * from the given Random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random long
	 */
	public static long nextLong(Random random) {
		return random.nextLong();
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed boolean value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random boolean
	 */
	public static boolean nextBoolean() {
		return nextBoolean(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed boolean value
	 * from the given random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random boolean
	 */
	public static boolean nextBoolean(Random random) {
		return random.nextBoolean();
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the Math.random() sequence.</p>
	 *
	 * @return the random float
	 */
	public static float nextFloat() {
		return nextFloat(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the given Random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random float
	 */
	public static float nextFloat(Random random) {
		return random.nextFloat();
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the Math.random() sequence.</p>
	 *
	 * @return the random double
	 */
	public static double nextDouble() {
		return nextDouble(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the given Random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random double
	 */
	public static double nextDouble(Random random) {
		return random.nextDouble();
	}

}
