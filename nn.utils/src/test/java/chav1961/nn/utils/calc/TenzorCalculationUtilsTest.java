package chav1961.nn.utils.calc;

import org.junit.Test;

import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.cdb.SyntaxNode;

import org.junit.Assert;

public class TenzorCalculationUtilsTest {
	@Test
	public void basicExpressionTest() throws SyntaxException {
		TenzorCalculationUtils.parseCalcExpression("1"); 
		
		try {
			TenzorCalculationUtils.parseCalcExpression(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try {
			TenzorCalculationUtils.parseCalcExpression("");
			Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	}

	@Test
	public void lexExpressionTest() throws SyntaxException {
		SyntaxNode<TenzorCalculationUtils.Command, SyntaxNode<?,?>> result = TenzorCalculationUtils.parseCalcExpression("1");
		
		Assert.assertEquals(TenzorCalculationUtils.Command.LoadConstant, result.getType());
		Assert.assertEquals(1, Double.longBitsToDouble(result.value), 0.001);

		result = TenzorCalculationUtils.parseCalcExpression("%1");
		
		Assert.assertEquals(TenzorCalculationUtils.Command.LoadTenzor, result.getType());
		Assert.assertEquals(1, result.value);

		result = TenzorCalculationUtils.parseCalcExpression("- 2");
		
		Assert.assertEquals(TenzorCalculationUtils.Command.UnaryOper, result.getType());
		Assert.assertTrue(result.cargo instanceof char[]);
		Assert.assertArrayEquals(new char[] {'-'}, (char[])result.cargo);
		Assert.assertEquals(1, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);

		result = TenzorCalculationUtils.parseCalcExpression("2 * 3");
		
		Assert.assertEquals(TenzorCalculationUtils.Command.MulOper, result.getType());
		Assert.assertTrue(result.cargo instanceof char[]);
		Assert.assertArrayEquals(new char[] {'*'}, (char[])result.cargo);
		Assert.assertEquals(2, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);
		Assert.assertEquals(3, Double.longBitsToDouble(result.children[1].value), 0.001);

		result = TenzorCalculationUtils.parseCalcExpression("2 + 3");
		
		Assert.assertEquals(TenzorCalculationUtils.Command.AddOper, result.getType());
		Assert.assertTrue(result.cargo instanceof char[]);
		Assert.assertArrayEquals(new char[] {'+'}, (char[])result.cargo);
		Assert.assertEquals(2, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);
		Assert.assertEquals(3, Double.longBitsToDouble(result.children[1].value), 0.001);

		result = TenzorCalculationUtils.parseCalcExpression("(2 + 3)");
		
		Assert.assertEquals(TenzorCalculationUtils.Command.AddOper, result.getType());
		Assert.assertTrue(result.cargo instanceof char[]);
		Assert.assertArrayEquals(new char[] {'+'}, (char[])result.cargo);
		Assert.assertEquals(2, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);
		Assert.assertEquals(3, Double.longBitsToDouble(result.children[1].value), 0.001);

		result = TenzorCalculationUtils.parseCalcExpression("sqrt(2)");
		
		Assert.assertEquals(TenzorCalculationUtils.Command.Function, result.getType());
		Assert.assertEquals(TenzorCalculationUtils.FunctionType.Sqrt, result.cargo);
		Assert.assertEquals(1, result.children.length);
		Assert.assertEquals(2, Double.longBitsToDouble(result.children[0].value), 0.001);
		
		try {
			TenzorCalculationUtils.parseCalcExpression("?");
			Assert.fail("Mandatory exception was not detected (unknown lexema)");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorCalculationUtils.parseCalcExpression("%s");
			Assert.fail("Mandatory exception was not detected (unknown lexema)");
		} catch (SyntaxException exc) {
		}
		
		try {
			TenzorCalculationUtils.parseCalcExpression("1 1");
			Assert.fail("Mandatory exception was not detected (dust in the tail)");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorCalculationUtils.parseCalcExpression("1 + + 1");
			Assert.fail("Mandatory exception was not detected (missing operand)");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorCalculationUtils.parseCalcExpression("- - 1");
			Assert.fail("Mandatory exception was not detected (missing operand)");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorCalculationUtils.parseCalcExpression("(1");
			Assert.fail("Mandatory exception was not detected (missing ')')");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorCalculationUtils.parseCalcExpression("sqrt 1");
			Assert.fail("Mandatory exception was not detected (missing '(')");
		} catch (SyntaxException exc) {
		}
		try {
			TenzorCalculationUtils.parseCalcExpression("sqrt(1");
			Assert.fail("Mandatory exception was not detected (missing ')')");
		} catch (SyntaxException exc) {
		}
	}
}
