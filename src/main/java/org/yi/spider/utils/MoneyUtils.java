package org.yi.spider.utils;

import org.apache.commons.lang.math.NumberUtils;

/**
 * 金额转中文大写
 * 
 * @author 潘瑞峥
 * @date 2011-9-29
 */
public class MoneyUtils {

	/** 大写数字 */
	private static final String[] NUMBERS = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };

	/** 整数部分的单位 */
	private static final String[] IUNIT = { "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟" };

	/** 小数部分的单位 */
	private static final String[] DUNIT = { "角", "分", "厘" };

	/**
	 * 得到大写金额。直接调用该静态方法得到大写的中文数字金额，如： String result = MoneyUtil.toChinese("12");则result="壹拾贰元";
	 * 
	 * @param numMoney
	 *            一个需要转换成中文大写的金额数字字符串
	 * @return 把一个非空的合法的数字字符串转换成中文大写金额后返回
	 * @throws Exception
	 * @throws 没有明确定义返回异常类型
	 *             ，如果出现如参数不合法，将会抛出运行时异常。
	 */
	public static String toChineseCapital( String numMoney ) throws Exception {
		// 是否为空或空字符串或为空格
		if ( StringUtils.isBlank( numMoney ) )
			throw new Exception( "当前传入的金额参数为[" + numMoney + "]，为空或空字符串或空格字符串，请检查！" );
		// 是否为数字格式
		else if ( !NumberUtils.isNumber( numMoney ) )
			throw new Exception( "当前传入的金额参数为[" + numMoney + "]，不为金额格式，请检查！" );
		// 去掉","
		numMoney = numMoney.trim().replaceAll( ",", "" );
		// 整数部分数字
		String integerStr;
		// 小数部分数字
		String decimalStr;

		// 初始化：分离整数部分和小数部分
		if ( numMoney.indexOf( "." ) > 0 ) {
			integerStr = numMoney.substring( 0, numMoney.indexOf( "." ) );
			decimalStr = numMoney.substring( numMoney.indexOf( "." ) + 1 );
		} else if ( numMoney.indexOf( "." ) == 0 ) {
			integerStr = "";
			decimalStr = numMoney.substring( 1 );
		} else {
			integerStr = numMoney;
			decimalStr = "";
		}
		// integerStr去掉首0，不必去掉decimalStr的尾0(超出部分舍去)
		if ( !integerStr.equals( "" ) ) {
			integerStr = Long.toString( Long.parseLong( integerStr ) );
			if ( integerStr.equals( "0" ) ) {
				integerStr = "";
			}
		}
		// overflow超出处理能力，直接返回
		if ( integerStr.length() > IUNIT.length ) {
			return numMoney;
		}
		// 整数部分数字
		int[] integers = toArray( integerStr );
		// 设置万单位
		boolean isMust5 = isMust5( integerStr );
		// 小数部分数字
		int[] decimals = toArray( decimalStr );
		return getChineseInteger( integers, isMust5 ) + getChineseDecimal( decimals );
	}

	/**
	 * 将数字金额转为中文大写
	 * 
	 * @param numMoney
	 * @return
	 * @throws Exception
	 */
	public static String toChineseCapital( double numMoney ) throws Exception {
		return toChineseCapital( String.valueOf( numMoney ) );
	}

	/**
	 * 整数部分和小数部分转换为数组，从高位至低位
	 */
	private static int[] toArray( String number ) {
		int[] array = new int[number.length()];
		for ( int i = 0; i < number.length(); i++ ) {
			array[ i ] = Integer.parseInt( number.substring( i, i + 1 ) );
		}
		return array;
	}

	/**
	 * 得到中文金额的整数部分。
	 */
	private static String getChineseInteger( int[] integers, boolean isMust5 ) {
		StringBuffer chineseInteger = new StringBuffer( "" );
		int length = integers.length;
		for ( int i = 0; i < length; i++ ) {
			// 0出现在关键位置：1234(万)5678(亿)9012(万)3456(元)
			// 特殊情况：10(拾元、壹拾元、壹拾万元、拾万元)
			String key = "";
			if ( integers[ i ] == 0 ) {
				// 万(亿)(必填)
				if ( ( length - i ) == 13 )
					key = IUNIT[ 4 ];
				// 亿(必填)
				else if ( ( length - i ) == 9 )
					key = IUNIT[ 8 ];
				// 万(不必填)
				else if ( ( length - i ) == 5 && isMust5 )
					key = IUNIT[ 4 ];
				// 元(必填)
				else if ( ( length - i ) == 1 )
					key = IUNIT[ 0 ];
				// 0遇非0时补零，不包含最后一位
				if ( ( length - i ) > 1 && integers[ i + 1 ] != 0 )
					key += NUMBERS[ 0 ];
			}
			chineseInteger.append( integers[ i ] == 0 ? key : ( NUMBERS[ integers[ i ] ] + IUNIT[ length - i - 1 ] ) );
		}
		return chineseInteger.toString();
	}

	/**
	 * 得到中文金额的小数部分。
	 */
	private static String getChineseDecimal( int[] decimals ) {
		StringBuffer chineseDecimal = new StringBuffer( "" );
		for ( int i = 0; i < decimals.length; i++ ) {
			// 舍去3位小数之后的
			if ( i == 3 )
				break;
			chineseDecimal.append( decimals[ i ] == 0 ? "" : ( NUMBERS[ decimals[ i ] ] + DUNIT[ i ] ) );
		}
		return chineseDecimal.toString();
	}

	/**
	 * 判断第5位数字的单位"万"是否应加。
	 */
	private static boolean isMust5( String integerStr ) {
		int length = integerStr.length();
		if ( length > 4 ) {
			String subInteger = "";
			if ( length > 8 ) {
				// 取得从低位数，第5到第8位的字串
				subInteger = integerStr.substring( length - 8, length - 4 );
			} else {
				subInteger = integerStr.substring( 0, length - 4 );
			}
			return Integer.parseInt( subInteger ) > 0;
		} else {
			return false;
		}
	}

}