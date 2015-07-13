package org.mtransit.parser.ca_ottawa_oc_transpo_bus;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.mt.data.MTrip;

// http://www.octranspo1.com/developers
// http://data.ottawa.ca/en/dataset/oc-transpo-schedules
// http://www.octranspo1.com/files/google_transit.zip
public class OttawaOCTranspoBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-ottawa-oc-transpo-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new OttawaOCTranspoBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("\nGenerating OC Transpo bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("\nGenerating OC Transpo bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public long getRouteId(GRoute gRoute) {
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		return Integer.parseInt(matcher.group());
	}

	private static final String RLN_SEPARATOR = "<->";
	private static final String ROUTE_1 = "South Keys " + RLN_SEPARATOR + " Ottawa-Rockcliffe";
	private static final String ROUTE_2 = "Downtown " + RLN_SEPARATOR + " Bayshore";
	private static final String ROUTE_4 = "Hurdman " + RLN_SEPARATOR + " Rideau Ctr";
	private static final String ROUTE_5 = "Billings Bridge " + RLN_SEPARATOR + " St Laurent";
	private static final String ROUTE_6 = "Hurdman " + RLN_SEPARATOR + " Tunney's Pasture";
	private static final String ROUTE_7 = "Downtown & Carleton " + RLN_SEPARATOR + " Brittany & St Laurent";
	private static final String ROUTE_8 = "Billings Bridge " + RLN_SEPARATOR + " Gatineau";
	private static final String ROUTE_9 = "Rideau Ctr " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_12 = "Blair " + RLN_SEPARATOR + " Rideau Ctr";
	private static final String ROUTE_14 = "St Laurent " + RLN_SEPARATOR + " Carlington";
	private static final String ROUTE_16 = "Main " + RLN_SEPARATOR + " Britannia";
	private static final String ROUTE_20 = "Downtown " + RLN_SEPARATOR + " Orléans";
	private static final String ROUTE_21 = "Downtown " + RLN_SEPARATOR + " Orléans";
	private static final String ROUTE_22 = "Downtown " + RLN_SEPARATOR + " Millennium";
	private static final String ROUTE_24 = "Downtown " + RLN_SEPARATOR + " Beacon Hl";
	private static final String ROUTE_27 = "Gatineau " + RLN_SEPARATOR + " Orléans";
	private static final String ROUTE_28 = "St Laurent " + RLN_SEPARATOR + " Rideau Ctr";
	private static final String ROUTE_30 = "Downtown " + RLN_SEPARATOR + " Jeanne d'Arc";
	private static final String ROUTE_31 = "Downtown " + RLN_SEPARATOR + " Jeanne d'Arc";
	private static final String ROUTE_34 = "Downtown " + RLN_SEPARATOR + " Jeanne d'Arc";
	private static final String ROUTE_35 = "Downtown " + RLN_SEPARATOR + " Orléans";
	private static final String ROUTE_37 = "Downtown " + RLN_SEPARATOR + " Jeanne d'Arc";
	private static final String ROUTE_38 = "Downtown " + RLN_SEPARATOR + " Jeanne d'Arc";
	private static final String ROUTE_40 = "Gatineau " + RLN_SEPARATOR + " Blossom Park";
	private static final String ROUTE_41 = "Downtown " + RLN_SEPARATOR + " Walkley";
	private static final String ROUTE_43 = "Downtown " + RLN_SEPARATOR + " Conroy";
	private static final String ROUTE_60 = "Downtown " + RLN_SEPARATOR + " Kanata";
	private static final String ROUTE_61 = "Downtown " + RLN_SEPARATOR + " Kanata";
	private static final String ROUTE_62 = "Downtown " + RLN_SEPARATOR + " Kanata";
	private static final String ROUTE_64 = "Downtown " + RLN_SEPARATOR + " Kanata";
	private static final String ROUTE_65 = "Downtown " + RLN_SEPARATOR + " Kanata";
	private static final String ROUTE_66 = "Downtown " + RLN_SEPARATOR + " Kanata";
	private static final String ROUTE_67 = "Downtown " + RLN_SEPARATOR + " Pinecrest";
	private static final String ROUTE_68 = "Downtown " + RLN_SEPARATOR + " Kanata";
	private static final String ROUTE_69 = "Downtown " + RLN_SEPARATOR + " Nepean Ctr";
	private static final String ROUTE_70 = "Downtown " + RLN_SEPARATOR + " Barrhaven";
	private static final String ROUTE_71 = "Downtown " + RLN_SEPARATOR + " Barrhaven";
	private static final String ROUTE_72 = "Downtown " + RLN_SEPARATOR + " Barrhaven";
	private static final String ROUTE_73 = "Downtown " + RLN_SEPARATOR + " Barrhaven";
	private static final String ROUTE_77 = "Downtown " + RLN_SEPARATOR + " Barrhaven";
	private static final String ROUTE_85 = "Hurdman " + RLN_SEPARATOR + " Bayshore";
	private static final String ROUTE_86 = "Elmvale " + RLN_SEPARATOR + " Baseline / Colonnade";
	private static final String ROUTE_87 = "South Keys " + RLN_SEPARATOR + " Baseline";
	private static final String ROUTE_91 = "Orléans & Trim " + RLN_SEPARATOR + " Baseline";
	private static final String ROUTE_92 = "St-Laurent " + RLN_SEPARATOR + " Terry Fox & Stittsville";
	private static final String ROUTE_93 = "Lincoln Fields " + RLN_SEPARATOR + " Kanata North / LeBreton";
	private static final String ROUTE_94 = "Riverview " + RLN_SEPARATOR + " Millennium";
	private static final String ROUTE_95 = "Orléans & Trim " + RLN_SEPARATOR + " Barrhaven Ctr";
	private static final String ROUTE_96 = "Blair, Hurdman " + RLN_SEPARATOR + " Terry Fox, Stittsville";
	private static final String ROUTE_97 = "Airport " + RLN_SEPARATOR + " Bayshore & Bells Corners";
	private static final String ROUTE_98 = "Hawthorne " + RLN_SEPARATOR + " Greenboro & Tunney's Pasture";
	private static final String ROUTE_99 = "Greenboro " + RLN_SEPARATOR + " Barrhaven / Manotick";
	private static final String ROUTE_101 = "St Laurent " + RLN_SEPARATOR + " Bayshore";
	private static final String ROUTE_105 = "Gatineau " + RLN_SEPARATOR + " Tunney's Pasture";
	private static final String ROUTE_106 = "Elmvale " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_107 = "Bayview " + RLN_SEPARATOR + " Southkeys";
	private static final String ROUTE_111 = "Billings Bridge/Carleton " + RLN_SEPARATOR + " Baseline";
	private static final String ROUTE_112 = "Elmvale " + RLN_SEPARATOR + " Billings Bridge";
	private static final String ROUTE_114 = "Hurdman " + RLN_SEPARATOR + " Greenboro";
	private static final String ROUTE_116 = "Greenboro & Hurdman " + RLN_SEPARATOR + " Merivale";
	private static final String ROUTE_118 = "Hurdman " + RLN_SEPARATOR + " Kanata";
	private static final String ROUTE_120 = "Portobello " + RLN_SEPARATOR + " Place d'Orléans";
	private static final String ROUTE_121 = "Blair " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_122 = "Millennium " + RLN_SEPARATOR + " Place d'Orléans";
	private static final String ROUTE_123 = "Gloucester North / Blair";
	private static final String ROUTE_124 = "Beacon Hl " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_126 = "Pineview " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_127 = "Blair " + RLN_SEPARATOR + " St Laurent";
	private static final String ROUTE_128 = "Blackburn Hamlet " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_129 = "Carson's " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_130 = "Millennium " + RLN_SEPARATOR + " Blair";
	private static final String ROUTE_131 = "Convent Glen " + RLN_SEPARATOR + " Chapel Hl";
	private static final String ROUTE_132 = "Place d'Orléans " + RLN_SEPARATOR + " Sunview";
	private static final String ROUTE_134 = "Renaud " + RLN_SEPARATOR + " Place d'Orléans";
	private static final String ROUTE_135 = "Esprit " + RLN_SEPARATOR + " Place d'Orléans";
	private static final String ROUTE_136 = "Tenth Line " + RLN_SEPARATOR + " Place d'Orléans";
	private static final String ROUTE_137 = "Queenswood Heights / Place d'Orléans";
	private static final String ROUTE_140 = "McCarthy " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_143 = "Conroy " + RLN_SEPARATOR + " South Keys";
	private static final String ROUTE_144 = "Leitrim " + RLN_SEPARATOR + " South Keys";
	private static final String ROUTE_146 = "South Keys " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_147 = "Uplands / South Keys";
	private static final String ROUTE_148 = "Elmvale " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_149 = "Elmvale " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_150 = "Tunney's Pasture " + RLN_SEPARATOR + " Lincoln Fields";
	private static final String ROUTE_151 = "Tunney's Pasture " + RLN_SEPARATOR + " Clyde";
	private static final String ROUTE_152 = "Lincoln Fields " + RLN_SEPARATOR + " Moodie & Bayshore";
	private static final String ROUTE_153 = "Carlingwood " + RLN_SEPARATOR + " Lincoln Fields";
	private static final String ROUTE_154 = "Queensway Terrace " + RLN_SEPARATOR + " Lincoln Fields";
	private static final String ROUTE_155 = "Queensway Terrace " + RLN_SEPARATOR + " Pinecrest/Bayshore";
	private static final String ROUTE_156 = "Baseline " + RLN_SEPARATOR + " Merivale";
	private static final String ROUTE_157 = "Baseline " + RLN_SEPARATOR + " Amberwood";
	private static final String ROUTE_159 = "Tunney's Pasture " + RLN_SEPARATOR + " Local";
	private static final String ROUTE_161 = "Bridlewood " + RLN_SEPARATOR + " Terry Fox";
	private static final String ROUTE_162 = "Terry Fox " + RLN_SEPARATOR + " Stittsville";
	private static final String ROUTE_165 = "Innovation " + RLN_SEPARATOR + " Terry Fox";
	private static final String ROUTE_164 = "Bridlewood " + RLN_SEPARATOR + " Terry Fox";
	private static final String ROUTE_168 = "Beaverbrook " + RLN_SEPARATOR + " Katimavik";
	private static final String ROUTE_170 = "Fallowfield " + RLN_SEPARATOR + " Barrhaven Ctr";
	private static final String ROUTE_171 = "Fallowfield " + RLN_SEPARATOR + " Barrhaven Ctr";
	private static final String ROUTE_172 = "Lincoln Fields " + RLN_SEPARATOR + " Bayshore";
	private static final String ROUTE_173 = "Barrhaven Ctr " + RLN_SEPARATOR + " Fallowfield & Bayshore";
	private static final String ROUTE_174 = "Baseline " + RLN_SEPARATOR + " Knoxdale";
	private static final String ROUTE_175 = "Golflinks " + RLN_SEPARATOR + " Barrhaven Ctr";
	private static final String ROUTE_176 = "Barrhaven Ctr " + RLN_SEPARATOR + " Tunney's Pasture";
	private static final String ROUTE_177 = "Barrhaven Ctr / Fallowfield " + RLN_SEPARATOR + " Cambrian";
	private static final String ROUTE_178 = "Lincoln Fields / Ctrpointe";
	private static final String ROUTE_180 = "Bayshore " + RLN_SEPARATOR + " Haanel";
	private static final String ROUTE_181 = "Eagleson " + RLN_SEPARATOR + " Hertzberg";
	private static final String ROUTE_182 = "Kanata " + RLN_SEPARATOR + " Lincoln Fields";
	private static final String ROUTE_185 = "LeBreton / Experimental Farm";
	private static final String ROUTE_186 = "Manotick " + RLN_SEPARATOR + " Barrhaven Ctr";
	private static final String ROUTE_188 = "Canadian Tire Ctr " + RLN_SEPARATOR + " Huntmar";
	private static final String ROUTE_189 = "Riverview " + RLN_SEPARATOR + " Greenboro";
	private static final String ROUTE_192 = "Hawthorne " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_193 = "Place d'Orléans " + RLN_SEPARATOR + " Blair";
	private static final String ROUTE_194 = "Gloucester North " + RLN_SEPARATOR + " Blair";
	private static final String ROUTE_196 = "Tanger " + RLN_SEPARATOR + " Terry Fox";
	private static final String ROUTE_198 = "Petrie Island " + RLN_SEPARATOR + " Place D'Orleans";
	private static final String ROUTE_199 = "Barrhaven " + RLN_SEPARATOR + " Place d'Orléans";
	private static final String ROUTE_201 = "Bayshore / Carlingwood " + RLN_SEPARATOR + " Stittsville / Richmond";
	private static final String ROUTE_202 = "Place d'Orléans / St Laurent " + RLN_SEPARATOR + " Navan / Sarsfield / Cumberland";
	private static final String ROUTE_203 = "Bayshore, Carlingwood " + RLN_SEPARATOR + " Stittsville, Dunrobin, Carp";
	private static final String ROUTE_204 = "South Keys / Billings Bridge " + RLN_SEPARATOR + " Greely / Metcalfe";
	private static final String ROUTE_205 = "Barrhaven / Carlingwood " + RLN_SEPARATOR + " Manotick / Kars / North Gower";
	private static final String ROUTE_221 = "Downtown " + RLN_SEPARATOR + " Cumberland";
	private static final String ROUTE_231 = "Downtown " + RLN_SEPARATOR + " Cumberland";
	private static final String ROUTE_232 = "Downtown " + RLN_SEPARATOR + " Vars";
	private static final String ROUTE_261 = "Downtown " + RLN_SEPARATOR + " Stittsville";
	private static final String ROUTE_262 = "Downtown " + RLN_SEPARATOR + " Stittsville";
	private static final String ROUTE_263 = "Downtown " + RLN_SEPARATOR + " Stittsville";
	private static final String ROUTE_283 = "Downtown " + RLN_SEPARATOR + " Richmond & Munster Hamlet";
	private static final String ROUTE_401 = "Canadian Tire Ctr";
	private static final String ROUTE_402 = "Canadian Tire Ctr";
	private static final String ROUTE_403 = "Canadian Tire Ctr";
	private static final String ROUTE_404 = "Canadian Tire Ctr";
	private static final String ROUTE_405 = "Canadian Tire Ctr";
	private static final String ROUTE_406 = "Canadian Tire Ctr";
	private static final String ROUTE_450 = "Lansdowne " + RLN_SEPARATOR + " Rideau Ctr";
	private static final String ROUTE_451 = "Lansdowne Park " + RLN_SEPARATOR + " Blair";
	private static final String ROUTE_452 = "Lansdowne Park " + RLN_SEPARATOR + " South Keys";
	private static final String ROUTE_454 = "Lansdowne " + RLN_SEPARATOR + " Terry Fox";
	private static final String ROUTE_455 = "Lansdowne " + RLN_SEPARATOR + " Trim";
	private static final String ROUTE_456 = "Lansdowne Park " + RLN_SEPARATOR + " Barrhaven Ctr";
	private static final String ROUTE_602 = "É. S De La Salle " + RLN_SEPARATOR + " Hurdman";
	private static final String ROUTE_611 = "É. S Gisèle Lalonde " + RLN_SEPARATOR + " Chapel Hl";
	private static final String ROUTE_612 = "É. S Gisèle Lalonde " + RLN_SEPARATOR + " Chapel Hl";
	private static final String ROUTE_613 = "Immaculata High School " + RLN_SEPARATOR + " Hurdman Sta";
	private static final String ROUTE_618 = "É. S Louis-Riel " + RLN_SEPARATOR + " Millennium Sta";
	private static final String ROUTE_619 = "É. S Louis-Riel " + RLN_SEPARATOR + " Blair Sta";
	private static final String ROUTE_622 = "Colonel By & Lester B. Pearson Schools " + RLN_SEPARATOR + " Renaud / Saddleridge";
	private static final String ROUTE_632 = "É. S Gisèle Lalonde " + RLN_SEPARATOR + " Queenswood Heights";
	private static final String ROUTE_633 = "Lester B. Pearson High School " + RLN_SEPARATOR + " St Laurent Sta";
	private static final String ROUTE_640 = "Brookfield High School " + RLN_SEPARATOR + " Greenboro Sta";
	private static final String ROUTE_641 = "É. S Louis Riel " + RLN_SEPARATOR + " Meadowglen / Orléans";
	private static final String ROUTE_648 = "É. S Louis-Riel " + RLN_SEPARATOR + " Youville / St Joseph";
	private static final String ROUTE_661 = "Bell High School " + RLN_SEPARATOR + " Terry Fox Sta";
	private static final String ROUTE_665 = "Bell High School < -> Bridlewood";
	private static final String ROUTE_669 = "Bell High School " + RLN_SEPARATOR + " Bayshore / Carling";
	private static final String ROUTE_670 = "St Pius X High School " + RLN_SEPARATOR + " Vaan / Woodroffe";
	private static final String ROUTE_674 = "All Saints Catholic & Stephen Leacock Schools " + RLN_SEPARATOR + " Innovation / Hines";
	private static final String ROUTE_678 = "É. S Louis-Riel " + RLN_SEPARATOR + " Jeanne d'Arc Sta";
	private static final String ROUTE_681 = "Bell High School " + RLN_SEPARATOR + " Bridlewood";
	private static final String ROUTE_691 = "É. S Deslauriers " + RLN_SEPARATOR + " Bayshore Sta";

	@Override
	public String getRouteLongName(GRoute gRoute) {
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		switch (digits) {
		// @formatter:off
		case 1: return ROUTE_1;
		case 2: return ROUTE_2;
		case 4: return ROUTE_4;
		case 5: return ROUTE_5;
		case 6: return ROUTE_6;
		case 7: return ROUTE_7;
		case 8: return ROUTE_8;
		case 9: return ROUTE_9;
		case 12: return ROUTE_12;
		case 14: return ROUTE_14;
		case 16: return ROUTE_16;
		case 18: return ROUTE_28;
		case 20: return ROUTE_20;
		case 21: return ROUTE_21;
		case 22: return ROUTE_22;
		case 24: return ROUTE_24;
		case 27: return ROUTE_27;
		case 30: return ROUTE_30;
		case 31: return ROUTE_31;
		case 34: return ROUTE_34;
		case 35: return ROUTE_35;
		case 37: return ROUTE_37;
		case 38: return ROUTE_38;
		case 40: return ROUTE_40;
		case 41: return ROUTE_41;
		case 43: return ROUTE_43;
		case 60: return ROUTE_60;
		case 61: return ROUTE_61;
		case 62: return ROUTE_62;
		case 64: return ROUTE_64;
		case 65: return ROUTE_65;
		case 66: return ROUTE_66;
		case 67: return ROUTE_67;
		case 68: return ROUTE_68;
		case 69: return ROUTE_69;
		case 70: return ROUTE_70;
		case 71: return ROUTE_71;
		case 72: return ROUTE_72;
		case 73: return ROUTE_73;
		case 77: return ROUTE_77;
		case 85: return ROUTE_85;
		case 86: return ROUTE_86;
		case 87: return ROUTE_87;
		case 91: return ROUTE_91;
		case 92: return ROUTE_92;
		case 93: return ROUTE_93;
		case 94: return ROUTE_94;
		case 95: return ROUTE_95;
		case 96: return ROUTE_96;
		case 97: return ROUTE_97;
		case 98: return ROUTE_98;
		case 99: return ROUTE_99;
		case 101: return ROUTE_101;
		case 105: return ROUTE_105;
		case 106: return ROUTE_106;
		case 107: return ROUTE_107;
		case 111: return ROUTE_111;
		case 112: return ROUTE_112;
		case 114: return ROUTE_114;
		case 116: return ROUTE_116;
		case 118: return ROUTE_118;
		case 120: return ROUTE_120;
		case 121: return ROUTE_121;
		case 122: return ROUTE_122;
		case 123: return ROUTE_123;
		case 124: return ROUTE_124;
		case 126: return ROUTE_126;
		case 127: return ROUTE_127;
		case 128: return ROUTE_128;
		case 129: return ROUTE_129;
		case 130: return ROUTE_130;
		case 131: return ROUTE_131;
		case 132: return ROUTE_132;
		case 134: return ROUTE_134;
		case 135: return ROUTE_135;
		case 136: return ROUTE_136;
		case 137: return ROUTE_137;
		case 140: return ROUTE_140;
		case 143: return ROUTE_143;
		case 144: return ROUTE_144;
		case 146: return ROUTE_146;
		case 147: return ROUTE_147;
		case 148: return ROUTE_148;
		case 149: return ROUTE_149;
		case 150: return ROUTE_150;
		case 151: return ROUTE_151;
		case 152: return ROUTE_152;
		case 153: return ROUTE_153;
		case 154: return ROUTE_154;
		case 155: return ROUTE_155;
		case 156: return ROUTE_156;
		case 157: return ROUTE_157;
		case 159: return ROUTE_159;
		case 161: return ROUTE_161;
		case 162: return ROUTE_162;
		case 164: return ROUTE_164;
		case 165: return ROUTE_165;
		case 168: return ROUTE_168;
		case 170: return ROUTE_170;
		case 171: return ROUTE_171;
		case 172: return ROUTE_172;
		case 173: return ROUTE_173;
		case 174: return ROUTE_174;
		case 175: return ROUTE_175;
		case 176: return ROUTE_176;
		case 177: return ROUTE_177;
		case 178: return ROUTE_178;
		case 180: return ROUTE_180;
		case 181: return ROUTE_181;
		case 182: return ROUTE_182;
		case 185: return ROUTE_185;
		case 186: return ROUTE_186;
		case 188: return ROUTE_188;
		case 189: return ROUTE_189;
		case 192: return ROUTE_192;
		case 193: return ROUTE_193;
		case 194: return ROUTE_194;
		case 196: return ROUTE_196;
		case 198: return ROUTE_198;
		case 199: return ROUTE_199;
		case 201: return ROUTE_201;
		case 202: return ROUTE_202;
		case 203: return ROUTE_203;
		case 204: return ROUTE_204;
		case 205: return ROUTE_205;
		case 221: return ROUTE_221;
		case 231: return ROUTE_231;
		case 232: return ROUTE_232;
		case 261: return ROUTE_261;
		case 262: return ROUTE_262;
		case 263: return ROUTE_263;
		case 283: return ROUTE_283;
		case 401: return ROUTE_401;
		case 402: return ROUTE_402;
		case 403: return ROUTE_403;
		case 404: return ROUTE_404;
		case 405: return ROUTE_405;
		case 406: return ROUTE_406;
		case 450: return ROUTE_450;
		case 451: return ROUTE_451;
		case 452: return ROUTE_452;
		case 454: return ROUTE_454;
		case 455: return ROUTE_455;
		case 456: return ROUTE_456;
		case 602: return ROUTE_602;
		case 611: return ROUTE_611;
		case 612: return ROUTE_612;
		case 613: return ROUTE_613;
		case 618: return ROUTE_618;
		case 619: return ROUTE_619;
		case 622: return ROUTE_622;
		case 632: return ROUTE_632;
		case 633: return ROUTE_633;
		case 640: return ROUTE_640;
		case 641: return ROUTE_641;
		case 648: return ROUTE_648;
		case 661: return ROUTE_661;
		case 665: return ROUTE_665;
		case 669: return ROUTE_669;
		case 670: return ROUTE_670;
		case 674: return ROUTE_674;
		case 678: return ROUTE_678;
		case 681: return ROUTE_681;
		case 691: return ROUTE_691;
		// @formatter:on
		default:
			System.out.printf("\n%s: getRouteLongName() > Unexpected route ID '%s' (%s)\n", gRoute.route_id, digits, gRoute);
			System.exit(-1);
			return null;
		}
	}

	private static final String AGENCY_COLOR = "A2211F";

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String ROUTE_COLOR_BLACK = "231F20";
	@Deprecated
	private static final String ROUTE_COLOR_GRAY = "5A5758";
	private static final String ROUTE_COLOR_ORANGE = "A33F26";
	private static final String ROUTE_COLOR_RED = "B31B18";
	private static final String ROUTE_COLOR_GREEN = "396027";
	@Deprecated
	private static final String ROUTE_COLOR_ORCHID = "968472";
	private static final String ROUTE_COLOR_DARK_ORCHID = "968472";
	private static final String ROUTE_COLOR_CORAL = "FF7F50";
	@Deprecated
	private static final String ROUTE_COLOR_DARK_RED = "8B0000";

	private static final Collection<Integer> BLACK_ROUTES = Arrays.asList(new Integer[] { //
			1, 2, 4, 5, 7, 8, 9, 12, 14, 16, 18, //
					85, 86, 87, 91, 92, 93, 94, 95, 96, 97, 98, 99, //
					101, 106, 107, 111, 112, 114, 116, 118, 120, 121, 122, 123, 124, //
					126, 127, 128, 129, 130, 131, 132, 134, 135, 137, 143, 144, 146, 147, 148, 149, //
					150, 151, 152, 153, 154, 156, 159, 161, 162, 164, 165, 168, 170, 171, 172, 173, 174, //
					175, 176, 177, 178, 185, 196, 198 //
			});

	private static final Collection<Integer> ORANGE_ROUTES = Arrays.asList(new Integer[] { //
			201, 202, 203, 204, 205 //
			});

	@Deprecated
	private static final Collection<Integer> GRAY_ROUTES = Arrays.asList(new Integer[] { //
			91, //
					120, 123, 132, 137, //
					154, 161, 162, 165, 174, 175, 178 //
			});

	private static final Collection<Integer> RED_ROUTES = Arrays.asList(new Integer[] { //
			6, 24, 40, 41, 43, 67, //
					105, 136, 140, 155, 157, 180, 181, 182, 186, 188, 189, 192, 193, 194, 199, //
					201, 202, 203, 204, 205 //
			});

	private static final Collection<Integer> GREEN_ROUTES = Arrays.asList(new Integer[] { //
			20, 21, 22, 27, 30, 31, 34, 35, 37, 38, //
					60, 61, 62, 64, 65, 66, 68, 69, 70, 71, 72, 73, 77, //
					221, 231, 232, //
					261, 262, 263, 283 //
			});

	private static final Collection<Integer> CTC_C400_ROUTES = Arrays.asList(new Integer[] { //
			401, 402, 403, 404, 405, 406 //
			});

	@Deprecated
	private static final Collection<Integer> TDP_ROUTES = Arrays.asList(new Integer[] { //
			450, 451, 452, 454, 455, 456 //
			});

	@Deprecated
	private static final Collection<Integer> RP_ROUTES = Arrays.asList(new Integer[] { //
			500, 502, 503, 509, 515, 520, 523, 524, 525, 526, 530, 535, 538, 541, 542, 543, //
					551, 552, 553, 555, 556, 557, 558, 559, 565 //
			});

	private static final Collection<Integer> SCHOOL_ROUTES = Arrays.asList(new Integer[] { //
					602, 611, 612, 613, 618, 619, 622, 632, 633, 640, 641, 648, //
					661, 665, 669, 670, 674, 678, 681, 691 //
			});

	@Override
	public String getRouteColor(GRoute gRoute) {
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		int routeId = Integer.parseInt(matcher.group());
		// @formatter:off
		if (SCHOOL_ROUTES.contains(routeId)) { return ROUTE_COLOR_CORAL; }
		else if (CTC_C400_ROUTES.contains(routeId)) { return ROUTE_COLOR_DARK_ORCHID; }
		else if (GREEN_ROUTES.contains(routeId)) { return ROUTE_COLOR_GREEN; }
		else if (ORANGE_ROUTES.contains(routeId)) { return ROUTE_COLOR_ORANGE; }
		else if (RED_ROUTES.contains(routeId)) { return ROUTE_COLOR_RED; }
		else if (BLACK_ROUTES.contains(routeId)) { return ROUTE_COLOR_BLACK; }
		else if (GRAY_ROUTES.contains(routeId)) { return ROUTE_COLOR_GRAY; }
		else if (RP_ROUTES.contains(routeId)) { return ROUTE_COLOR_DARK_RED; }
		else if (TDP_ROUTES.contains(routeId)) { return ROUTE_COLOR_ORCHID; }
		// @formatter:on
		else {
			System.out.printf("\n%s: getRouteColor() > No color for route '%s'!", gRoute.route_id, gRoute);
			System.exit(-1);
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.trip_headsign), gTrip.direction_id);
	}

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		return tripHeadsign; // DO NOT CLEAN, USED TO IDENTIFY TRIP IN REAL TIME API
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		if (mTrip.getHeadsignValue() == null || mTrip.getHeadsignValue().equals(mTripToMerge.getHeadsignValue())) {
			System.out.printf("\nmergeHeadsign() > Can't merge headsign for trips %s and %s!\n", mTrip, mTripToMerge);
			System.exit(-1);
			return false; // DO NOT MERGE, USED TO IDENTIY TRIP IN REAL TIME API
		}
		return super.mergeHeadsign(mTrip, mTripToMerge);
	}

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = gStopName.toLowerCase(Locale.ENGLISH);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	private static final String EE = "EE";
	private static final String EO = "EO";
	private static final String NG = "NG";
	private static final String NO = "NO";
	private static final String WA = "WA";
	private static final String WD = "WD";
	private static final String WH = "WH";
	private static final String WI = "WI";
	private static final String WL = "WL";
	private static final String PLACE = "place";
	private static final String RZ = "RZ";

	@Override
	public int getStopId(GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode != null && stopCode.length() > 0 && Utils.isDigitsOnly(stopCode)) {
			return Integer.valueOf(stopCode); // using stop code as stop ID
		}
		Matcher matcher = DIGITS.matcher(gStop.stop_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		int stopId = 0;
		if (gStop.stop_id.startsWith(EE)) {
			stopId = 100000;
		} else if (gStop.stop_id.startsWith(EO)) {
			stopId = 200000;
		} else if (gStop.stop_id.startsWith(NG)) {
			stopId = 300000;
		} else if (gStop.stop_id.startsWith(NO)) {
			stopId = 400000;
		} else if (gStop.stop_id.startsWith(WA)) {
			stopId = 500000;
		} else if (gStop.stop_id.startsWith(WD)) {
			stopId = 600000;
		} else if (gStop.stop_id.startsWith(WH)) {
			stopId = 700000;
		} else if (gStop.stop_id.startsWith(WI)) {
			stopId = 800000;
		} else if (gStop.stop_id.startsWith(WL)) {
			stopId = 900000;
		} else if (gStop.stop_id.startsWith(PLACE)) {
			stopId = 1000000;
		} else if (gStop.stop_id.startsWith(RZ)) {
			stopId = 1100000;
		} else {
			System.out.println("getStopId() > Stop doesn't have an ID (start with)! " + gStop);
			System.exit(-1);
			stopId = -1;
		}
		return stopId + digits;
	}
}
