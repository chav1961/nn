package chav1961.nn.utils.calc;

import org.junit.Test;

import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.cdb.SyntaxNode;

import org.junit.Assert;

public class TenzorUtilsTest {
	@Test
	public void basicExpressionTest() throws SyntaxException {
		TenzorUtils.parseCalcExpression("1"); 
		
		try {
			TenzorUtils.parseCalcExpression(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try {
			TenzorUtils.parseCalcExpression("");
			Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	}

	@Test
	public void lexExpressionTest() throws SyntaxException {
		SyntaxNode<TenzorUtils.Command, SyntaxNode<?,?>> result = TenzorUtils.parseCalcExpression("1");
		
		Assert.assertEquals(TenzorUtils.Command.LoadConstant, result.getType());
		Assert.assertEquals(1, Double.longBitsToDouble(result.value), 0.001);

		result = TenzorUtils.parseCalcExpression("%1");
		
		Assert.assertEquals(TenzorUtils.Command.LoadTenzor, result.getType());
		Assert.assertEquals(1, result.value);

		result = TenzorUtils.parseCalcExpression("- 2");
		
		Assert.assertEquals(TenzorUtils.Command.UnaryOper, result.getType());
		Assert.assertTrue(result.cargo instanceof char[]);
		Assert.assertArrayEquals(new char[] {'-'}, (char[])result.cargo);
		Assert.assertEquals(1, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);

		result = TenzorUtils.parseCalcExpression("2 * 3");
		
		Assert.assertEquals(TenzorUtils.Command.MulOper, result.getType());
		Assert.assertTrue(result.cargo instanceof char[]);
		Assert.assertArrayEquals(new char[] {'*'}, (char[])result.cargo);
		Assert.assertEquals(2, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);
		Assert.assertEquals(3, Double.longBitsToDouble(result.children[1].value), 0.001);

		result = TenzorUtils.parseCalcExpression("2 + 3");
		
		Assert.assertEquals(TenzorUtils.Command.AddOper, result.getType());
		Assert.assertTrue(result.cargo instanceof char[]);
		Assert.assertArrayEquals(new char[] {'+'}, (char[])result.cargo);
		Assert.assertEquals(2, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);
		Assert.assertEquals(3, Double.longBitsToDouble(result.children[1].value), 0.001);

		result = TenzorUtils.parseCalcExpression("(2 + 3)");
		
		Assert.assertEquals(TenzorUtils.Command.AddOper, result.getType());
		Assert.assertTrue(result.cargo instanceof char[]);
		Assert.assertArrayEquals(new char[] {'+'}, (char[])result.cargo);
		Assert.assertEquals(2, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);
		Assert.assertEquals(3, Double.longBitsToDouble(result.children[1].value), 0.001);

		result = TenzorUtils.parseCalcExpression("sqrt(2)");
		
		Assert.assertEquals(TenzorUtils.Command.Function, result.getType());
		Assert.assertEquals(TenzorUtils.FunctionType.Sqrt, result.cargo);
		Assert.assertEquals(1, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);
		
		try {
			TenzorUtils.parseCalcExpression("?");
			Assert.fail("Mandatory exception was not detected (unknown lexema)");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorUtils.parseCalcExpression("%s");
			Assert.fail("Mandatory exception was not detected (unknown lexema)");
		} catch (SyntaxException exc) {
		}
		
		try {
			TenzorUtils.parseCalcExpression("1 1");
			Assert.fail("Mandatory exception was not detected (dust in the tail)");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorUtils.parseCalcExpression("1 + + 1");
			Assert.fail("Mandatory exception was not detected (missing operand)");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorUtils.parseCalcExpression("- - 1");
			Assert.fail("Mandatory exception was not detected (missing operand)");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorUtils.parseCalcExpression("(1");
			Assert.fail("Mandatory exception was not detected (missing ')')");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorUtils.parseCalcExpression("sqrt 1");
			Assert.fail("Mandatory exception was not detected (missing '(')");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorUtils.parseCalcExpression("sqrt(1");
			Assert.fail("Mandatory exception was not detected (missing ')')");
		} catch (SyntaxException exc) {
		}
	}
}
