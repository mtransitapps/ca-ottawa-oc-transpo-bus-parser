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
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MSpec;
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
		System.out.printf("Generating OC Transpo bus data...\n");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("Generating OC Transpo bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
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
		int digits = Integer.parseInt(matcher.group());
		int routeId;
		routeId = 0;
		return routeId + digits;
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		switch (digits) {
		case 1:
			return "South Keys <-> Ottawa-Rockcliffe";
		case 2:
			return "Downtown <-> Bayshore";
		case 4:
			return "Hurdman <-> Rideau Centre";
		case 5:
			return "Billings Bridge <-> St. Laurent";
		case 6:
			return "Hurdman <-> Tunney's Pasture";
		case 7:
			return "Downtown & Carleton <-> Brittany & St. Laurent";
		case 8:
			return "Billings Bridge <-> Gatineau";
		case 9:
			return "Rideau Centre <-> Hurdman";
		case 12:
			return "Blair <-> Rideau Centre";
		case 14:
			return "St. Laurent <-> Carlington";
		case 16:
			return "Main <-> Britannia";
		case 18:
			return "St. Laurent <-> Rideau Centre";
		case 20:
			return "Downtown <-> Orléans";
		case 21:
			return "Downtown <-> Orléans";
		case 22:
			return "Downtown <-> Millennium";
		case 24:
			return "Downtown <-> Beacon Hill";
		case 27:
			return "Gatineau <-> Orléans";
		case 30:
			return "Downtown <-> Jeanne d'Arc";
		case 31:
			return "Downtown <-> Jeanne d'Arc";
		case 34:
			return "Downtown <-> Jeanne d'Arc";
		case 35:
			return "Downtown <-> Orléans";
		case 37:
			return "Downtown <-> Jeanne d'Arc";
		case 38:
			return "Downtown <-> Jeanne d'Arc";
		case 40:
			return "Gatineau <-> Blossom Park";
		case 41:
			return "Downtown <-> Walkley";
		case 43:
			return "Downtown <-> Conroy";
		case 60:
			return "Downtown <-> Kanata";
		case 61:
			return "Downtown <-> Kanata";
		case 62:
			return "Downtown <-> Kanata";
		case 64:
			return "Downtown <-> Kanata";
		case 65:
			return "Downtown <-> Kanata";
		case 66:
			return "Downtown <-> Kanata";
		case 67:
			return "Downtown <-> Pinecrest";
		case 68:
			return "Downtown <-> Kanata";
		case 69:
			return "Downtown <-> Nepean Centre";
		case 70:
			return "Downtown <-> Barrhaven";
		case 71:
			return "Downtown <-> Barrhaven";
		case 72:
			return "Downtown <-> Barrhaven";
		case 73:
			return "Downtown <-> Barrhaven";
		case 77:
			return "Downtown <-> Barrhaven";
		case 85:
			return "Hurdman <-> Bayshore";
		case 86:
			return "Elmvale <-> Baseline/Colonnade";
		case 87:
			return "South Keys <-> Baseline";
		case 93:
			return "Lincoln Fields <-> Kanata North / LeBreton";
		case 94:
			return "Riverview <-> Millennium";
		case 95:
			return "Orléans & Trim <-> Barrhaven Centre";
		case 96:
			return "Blair, Hurdman <-> Terry Fox, Stittsville";
		case 97:
			return "Airport <-> Bayshore & Bells Corners";
		case 98:
			return "Hawthorne <-> Greenboro & Tunney's Pasture";
		case 99:
			return "Greenboro <-> Barrhaven / Manotick";
		case 101:
			return "St. Laurent <-> Bayshore";
		case 105:
			return "Gatineau <-> Tunney's Pasture";
		case 106:
			return "Elmvale <-> Hurdman";
		case 107:
			return "Bayview <-> Southkeys";
		case 111:
			return "Billings Bridge/Carleton <-> Baseline";
		case 112:
			return "Elmvale <-> Billings Bridge";
		case 114:
			return "Hurdman <-> Greenboro";
		case 116:
			return "Greenboro & Hurdman <-> Merivale";
		case 118:
			return "Hurdman <-> Kanata";
		case 120:
			return "Portobello <-> Place d'Orléans";
		case 121:
			return "Blair <-> Hurdman";
		case 122:
			return "Millennium <-> Place d'Orléans";
		case 123:
			return "Gloucester North / Blair";
		case 124:
			return "Beacon Hill <-> Hurdman";
		case 126:
			return "Pineview <-> Hurdman";
		case 127:
			return "Blair <-> St. Laurent";
		case 128:
			return "Blackburn Hamlet <-> Hurdman";
		case 129:
			return "Carson's <-> Hurdman";
		case 130:
			return "Millennium <-> Blair";
		case 131:
			return "Convent Glen <-> Chapel Hill";
		case 132:
			return "Place d'Orléans <-> Sunview";
		case 134:
			return "Renaud <-> Place d'Orléans";
		case 135:
			return "Esprit <-> Place d'Orléans";
		case 136:
			return "Tenth Line <-> Place d'Orléans";
		case 137:
			return "Queenswood Heights / Place d'Orléans";
		case 140:
			return "McCarthy <-> Hurdman";
		case 143:
			return "Conroy <-> South Keys";
		case 144:
			return "Leitrim <-> South Keys";
		case 146:
			return "South Keys <-> Hurdman";
		case 147:
			return "Uplands / South Keys";
		case 148:
			return "Elmvale <-> Hurdman";
		case 149:
			return "Elmvale <-> Hurdman";
		case 150:
			return "Tunney's Pasture <-> Lincoln Fields";
		case 151:
			return "Tunney's Pasture <-> Clyde";
		case 152:
			return "Lincoln Fields <-> Moodie & Bayshore";
		case 153:
			return "Carlingwood <-> Lincoln Fields";
		case 154:
			return "Queensway Terrace <-> Lincoln Fields";
		case 155:
			return "Queensway Terrace <-> Pinecrest/Bayshore";
		case 156:
			return "Baseline <-> Merivale";
		case 157:
			return "Baseline <-> Amberwood";
		case 159:
			return "Tunney's Pasture <-> Local";
		case 161:
			return "Bridlewood <-> Terry Fox";
		case 164:
			return "Bridlewood <-> Terry Fox";
		case 168:
			return "Beaverbrook <-> Katimavik";
		case 170:
			return "Fallowfield <-> Barrhaven Centre";
		case 171:
			return "Fallowfield <-> Barrhaven Centre";
		case 172:
			return "Lincoln Fields <-> Bayshore";
		case 173:
			return "Barrhaven Centre <-> Fallowfield & Bayshore";
		case 174:
			return "Baseline <-> Knoxdale";
		case 175:
			return "Golflinks <-> Barrhaven Centre";
		case 176:
			return "Barrhaven Centre <-> Tunney's Pasture";
		case 177:
			return "Barrhaven Centre / Fallowfield <-> Cambrian ";
		case 178:
			return "Lincoln Fields / Centrepointe";
		case 180:
			return "Bayshore <-> Haanel";
		case 181:
			return "Eagleson <-> Hertzberg";
		case 182:
			return "Kanata <-> Lincoln Fields";
		case 186:
			return "Manotick <-> Barrhaven Centre";
		case 188:
			return "Canadian Tire Centre <-> Huntmar";
		case 189:
			return "Riverview <-> Greenboro";
		case 192:
			return "Hawthorne <-> Hurdman";
		case 193:
			return "Place d'Orléans <-> Blair";
		case 194:
			return "Gloucester North <-> Blair";
		case 196:
			return "Tanger <-> Terry Fox";
		case 198:
			return "Petrie Island <-> Place D'Orleans";
		case 199:
			return "Barrhaven <-> Place d'Orléans";
		case 201:
			return "Bayshore / Carlingwood <-> Stittsville / Richmond";
		case 202:
			return "Place d'Orléans / St. Laurent <-> Navan / Sarsfield / Cumberland";
		case 203:
			return "Bayshore, Carlingwood <-> Stittsville, Dunrobin, Carp";
		case 204:
			return "South Keys / Billings Bridge <-> Greely / Metcalfe";
		case 205:
			return "Barrhaven / Carlingwood <-> Manotick / Kars / North Gower";
		case 221:
			return "Downtown <-> Cumberland";
		case 231:
			return "Downtown <-> Cumberland";
		case 232:
			return "Downtown <-> Vars";
		case 261:
			return "Downtown <-> Stittsville";
		case 262:
			return "Downtown <-> Stittsville";
		case 263:
			return "Downtown <-> Stittsville";
		case 283:
			return "Downtown <-> Richmond & Munster Hamlet";
		case 401:
			return "Canadian Tire Centre";
		case 402:
			return "Canadian Tire Centre";
		case 403:
			return "Canadian Tire Centre";
		case 404:
			return "Canadian Tire Centre";
		case 405:
			return "Canadian Tire Centre";
		case 406:
			return "Canadian Tire Centre";
		case 450:
			return "Lansdowne <-> Rideau Centre";
		case 451:
			return "Lansdowne Park <-> Blair";
		case 452:
			return "Lansdowne Park <-> South Keys";
		case 454:
			return "Lansdowne <-> Terry Fox";
		case 455:
			return "Lansdowne <-> Trim";
		case 456:
			return "Lansdowne Park <-> Barrhaven Centre";
		case 602:
			return "É. S De La Salle <-> Hurdman";
		case 611:
			return "É. S Gisèle Lalonde <-> Chapel Hill";
		case 612:
			return "É. S Gisèle Lalonde <-> Chapel Hill";
		case 613:
			return "Immaculata High School <-> Hurdman Station";
		case 618:
			return "É. S Louis-Riel <-> Millennium Station";
		case 619:
			return "É. S Louis-Riel <-> Blair Station";
		case 622:
			return "Colonel By & Lester B. Pearson Schools <-> Renaud / Saddleridge";
		case 632:
			return "É. S Gisèle Lalonde <-> Queenswood Heights";
		case 633:
			return "Lester B. Pearson High School <-> St. Laurent Station";
		case 640:
			return "Brookfield High School <-> Greenboro Station";
		case 641:
			return "É. S Louis Riel <-> Meadowglen / Orléans";
		case 648:
			return "É. S Louis-Riel <-> Youville / St. Joseph";
		case 661:
			return "Bell High School <-> Terry Fox Station";
		case 665:
			return "Bell High School < -> Bridlewood";
		case 669:
			return "Bell High School <-> Bayshore / Carling";
		case 670:
			return "St. Pius X High School <-> Vaan / Woodroffe";
		case 674:
			return "All Saints Catholic & Stephen Leacock Schools <-> Innovation / Hines";
		case 678:
			return "É. S Louis-Riel <-> Jeanne d'Arc Station";
		case 681:
			return "Bell High School <-> Bridlewood";
		case 691:
			return "É. S Deslauriers <-> Bayshore Station";
		case 750:
			return "O-Train Bayview <-> Greenboro";
		default:
			System.out.println("getRouteLongName() > Unexpected route ID '" + digits + "' (" + gRoute + ")");
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
					85, 86, 87, 93, 94, 95, 96, 97, 98, 99, //
					101, 106, 107, 111, 112, 114, 116, 118, 120, 121, 122, 123, 124, //
					126, 127, 128, 129, 130, 131, 132, 134, 135, 137, 143, 144, 146, 147, 148, 149, //
					150, 151, 152, 153, 154, 156, 159, 161, 164, 168, 170, 171, 172, 173, 174, //
					175, 176, 177, 178, 185, 196, 198 //
			});

	private static final Collection<Integer> ORANGE_ROUTES = Arrays.asList(new Integer[] { //
			201, 202, 203, 204, 205 //
			});

	@Deprecated
	private static final Collection<Integer> GRAY_ROUTES = Arrays.asList(new Integer[] { //
			120, 123, 132, 137, //
					154, 161, 174, 175, 178 //
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
		else if (CTC_C400_ROUTES.contains(routeId)) {return ROUTE_COLOR_DARK_ORCHID; }
		else if (GREEN_ROUTES.contains(routeId)) {return ROUTE_COLOR_GREEN; }
		else if (ORANGE_ROUTES.contains(routeId)) {return ROUTE_COLOR_ORANGE; }
		else if (RED_ROUTES.contains(routeId)) {return ROUTE_COLOR_RED; }
		else if (BLACK_ROUTES.contains(routeId)) {return ROUTE_COLOR_BLACK; }
		else if (GRAY_ROUTES.contains(routeId)) {return ROUTE_COLOR_GRAY; }
		else if (RP_ROUTES.contains(routeId)) {return ROUTE_COLOR_DARK_RED; }
		else if (TDP_ROUTES.contains(routeId)) {return ROUTE_COLOR_ORCHID; }
		// @formatter:on
		else {
			System.out.println("No color for route " + gRoute + "!");
			System.exit(-1);
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public void setTripHeadsign(MRoute route, MTrip mTrip, GTrip gTrip) {
		String stationName = cleanTripHeadsign(gTrip.trip_headsign);
		int directionId = Integer.valueOf(gTrip.direction_id);
		mTrip.setHeadsignString(stationName, directionId);
	}

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		return MSpec.cleanLabel(tripHeadsign); // TODO clean FR?
	}

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = gStopName.toLowerCase(Locale.ENGLISH);
		return super.cleanStopName(gStopName); // TODO clean FR?
	}

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
		if (gStop.stop_id.startsWith("EE")) {
			stopId = 100000;
		} else if (gStop.stop_id.startsWith("EO")) {
			stopId = 200000;
		} else if (gStop.stop_id.startsWith("NG")) {
			stopId = 300000;
		} else if (gStop.stop_id.startsWith("NO")) {
			stopId = 400000;
		} else if (gStop.stop_id.startsWith("WA")) {
			stopId = 500000;
		} else if (gStop.stop_id.startsWith("WD")) {
			stopId = 600000;
		} else if (gStop.stop_id.startsWith("WH")) {
			stopId = 700000;
		} else if (gStop.stop_id.startsWith("WI")) {
			stopId = 800000;
		} else if (gStop.stop_id.startsWith("WL")) {
			stopId = 900000;
		} else {
			System.out.println("Stop doesn't have an ID (start with)! " + gStop);
			System.exit(-1);
			stopId = -1;
		}
		return stopId + digits;
	}
}
