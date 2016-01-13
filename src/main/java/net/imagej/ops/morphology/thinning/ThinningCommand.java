/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, University of Konstanz and Brian Northan.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.ops.morphology.thinning;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.menu.MenuConstants;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.morphology.thinning.strategies.ThinningStrategy;
import net.imagej.ops.special.Computers;
import net.imagej.ops.special.UnaryComputerOp;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

/**
 * Thinning Operation
 *
 * @author Andreas Burger, University of Konstanz
 * @author Kyle Harrington, Beth Israel Deaconess Medical Center
 */
@Plugin(type = Command.class,
	menu = {
		@Menu(label = MenuConstants.PLUGINS_LABEL, weight = MenuConstants.PLUGINS_WEIGHT),
		@Menu(label = "Thinning") })
public class ThinningCommand implements Command {

	@Parameter
	private Dataset input;
	//private RandomAccessibleInterval<BitType> input;
	
	@Parameter( type=ItemIO.OUTPUT )
	private RandomAccessibleInterval<BitType> output;
	
	@Parameter
	private OpService ops;
	
	@Parameter
	private boolean m_foreground = true;

	@Parameter
	private boolean m_background = false;

	@Parameter(label = "ThinningStrategy")
	private ThinningStrategy m_strategy;
	

	public void run() {
		output = ops.create().img( input.getImgPlus().getImg(), new BitType() );
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		RandomAccessibleInterval<BitType> bitInput = 
			makeBitType( (Img) input.getImgPlus().getImg());
		
		ops.morphology().thinning( output, bitInput, m_foreground, m_background, m_strategy );
	}
			

	private <T extends RealType<T>> RandomAccessibleInterval<BitType> makeBitType( final Img<T> in)
	{
		final UnaryComputerOp<T, BitType> bitOp = Computers.unary(ops,
			Ops.Convert.Bit.class, new BitType(), in.firstElement());
		
		Img<BitType> out = ops.create().img( in, new BitType() );
		ops.map( (RandomAccessibleInterval<BitType>) out, in, bitOp);
		return out;
	}
	

}
