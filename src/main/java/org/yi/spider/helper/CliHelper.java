package org.yi.spider.helper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.yi.spider.enums.ParamEnum;
import org.yi.spider.utils.StringUtils;

public class CliHelper {

	private static Options options ;
	
	static {
		
		options = new Options();
		
		for(ParamEnum e:ParamEnum.values()) {
			if(StringUtils.isEmpty(e.getValueName())) {
				Option option = new Option(e.getName(), e.isHasArgs(),  e.getDesc());
				options.addOption(option);
			} else {
				OptionBuilder.withArgName(e.getValueName());
				OptionBuilder.hasArg();
				OptionBuilder.withDescription(e.getDesc());
				options.addOption(OptionBuilder.create(e.getName()));
			}
		}
	}
	
	public static Options getOptions() {
		return options;
	}
	
	public static CommandLine parse(String[] args) throws ParseException {
		
		CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args);
        
		return cmd;
	}
	
	
}
