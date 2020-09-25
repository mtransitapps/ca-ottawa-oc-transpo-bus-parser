package org.mtransit.parser.ca_ottawa_oc_transpo_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.Constants;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.StringUtils;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MDirectionType;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://www.octranspo.com/en/plan-your-trip/travel-tools/developers/
// https://www.octranspo.com/fr/planifiez/outils-dinformation/developpeurs/
// https://www.octranspo.com/files/google_transit.zip
public class OttawaOCTranspoBusAgencyTools extends DefaultAgencyTools {

	public static void main(@Nullable String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-ottawa-oc-transpo-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new OttawaOCTranspoBusAgencyTools().start(args);
	}

	private HashSet<Integer> serviceIds;

	@Override
	public void start(@NotNull String[] args) {
		MTLog.log("Generating OC Transpo bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIdInts(args, this, true);
		super.start(args);
		MTLog.log("Generating OC Transpo bus data... DONE in %s.", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIds != null && this.serviceIds.isEmpty();
	}

	@Override
	public boolean excludeCalendar(@NotNull GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarInt(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(@NotNull GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDateInt(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(@NotNull GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTripInt(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public boolean excludeRoute(@NotNull GRoute gRoute) {
		return super.excludeRoute(gRoute);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		if (!Utils.isDigitsOnly(gRoute.getRouteShortName())) {
			if ("R1".equalsIgnoreCase(gRoute.getRouteShortName())) { // 701
				return 701L;
			} else if ("Hurd".equalsIgnoreCase(gRoute.getRouteShortName())) { // 104
				return 104L;
			}
		} else {
			return Integer.parseInt(gRoute.getRouteShortName());
		}
		throw new MTLog.Fatal("Unexpected route ID for '%s'!", gRoute);
	}

	private static final String RLN_SEPARATOR = "-";
	private static final String RLN_SEP = " " + RLN_SEPARATOR + " ";
	private static final String AND = " & ";
	private static final String COLON = ", ";
	private static final String SLASH = " / ";
	private static final String HIGH_SCHOOL = "High School";
	private static final String SCHOOLS = "Schools";
	//
	private static final String ALBERT = "Albert";
	private static final String AIRPORT = "Airport";
	private static final String ALL_STS_CATHOLIC_AND_STEPHEN_LEACOCK_SCHOOLS = "All Sts Catholic" + AND + "Stephen Leacock " + SCHOOLS;
	private static final String AMBERWOOD = "Amberwood";
	private static final String BANK = "Bank";
	private static final String BARRHAVEN = "Barrhaven";
	private static final String BARRHAVEN_CTR = BARRHAVEN + " Ctr";
	private static final String BASELINE = "Baseline";
	private static final String BAY = "Bay";
	private static final String BAYSHORE = "Bayshore";
	private static final String BAYSHORE_STA = BAYSHORE + " Sta";
	private static final String BAYVIEW = "Bayview";
	private static final String BEACON_HILL = "Beacon Hl";
	private static final String BEAVERBROOK = "Beaverbrook";
	private static final String BELL_HIGH_SCHOOL = "Bell " + HIGH_SCHOOL;
	private static final String BELLS_CORNERS = "Bells Corners";
	private static final String BILLINGS_BRIDGE = "Billings Bridge";
	private static final String BLACKBURN = "Blackburn";
	private static final String BLAIR = "Blair";
	private static final String BLAIR_STA = BLAIR + " Sta";
	private static final String BLOSSOM_PARK = "Blossom Pk";
	private static final String BRIDLEWOOD = "Bridlewood";
	private static final String BRITANNIA = "Britannia";
	private static final String BROOKFIELD_HIGH_SCHOOL = "Brookfield " + HIGH_SCHOOL;
	private static final String CAIRINE_WILSON_HIGH_SCHOOL = "Cairine Wilson " + HIGH_SCHOOL;
	private static final String CAMBRIAN = "Cambrian";
	private static final String CANADIAN_TIRE_CTR = "Canadian Tire Ctr";
	private static final String CANTERBURY = "Canterbury";
	private static final String CANTERBURY_HIGH_SCHOOL = CANTERBURY + " " + HIGH_SCHOOL;
	private static final String CARLETON = "Carleton";
	private static final String CARLING = "Carling";
	private static final String CARLINGTON = "Carlington";
	private static final String CARLINGWOOD = "Carlingwood";
	private static final String CARP = "Carp";
	private static final String CARSON_S = "Carson's";
	private static final String CITIGATE = "CitiGate";
	private static final String CHAPEL_HL = "Chapel Hl";
	private static final String CLYDE = "Clyde";
	private static final String COLONEL_BY = "Colonel By";
	private static final String COLONEL_BY_HIGH_SCHOOL = COLONEL_BY + " " + HIGH_SCHOOL;
	private static final String COLONNADE = "Colonnade";
	private static final String CONROY = "Conroy";
	private static final String CONVENT_GLN = "Convent Gln";
	private static final String CTRPOINTE = "Ctrpointe";
	private static final String CUMBERLAND = "Cumberland";
	private static final String DOWNTOWN = "Downtown";
	private static final String DUNROBIN = "Dunrobin";
	private static final String E = "ÉS";
	private static final String E_S_DE_LA_SALLE = E + " De La Salle";
	private static final String E_S_DESLAURIERS = E + " Deslauriers";
	private static final String E_S_GISELE_LALONDE = E + " Gisèle Lalonde";
	private static final String E_S_LOUIS_RIEL = E + " Louis-Riel";
	private static final String EAGLESON = "Eagleson";
	private static final String ELMVALE = "Elmvale";
	private static final String ESPRIT = "Esprit";
	private static final String EXPERIMENTAL_FARM = "Experimental Farm";
	private static final String FALLOWFIELD = "Fallowfield";
	private static final String GARDENWAY = "Gardenway";
	private static final String GATINEAU = "Gatineau";
	private static final String GLOUCESTER = "Gloucester";
	private static final String GLOUCESTER_NORTH = GLOUCESTER + " North";
	private static final String GLOUCESTER_HIGH_SCHOOL = GLOUCESTER + " " + HIGH_SCHOOL;
	private static final String GOLFLINKS = "Golflinks";
	private static final String GRANDVIEW = "Grandview";
	private static final String GREELY = "Greely";
	private static final String GREENBORO = "Greenboro";
	private static final String GREENBORO_STA = GREENBORO + " Sta";
	private static final String HAANEL = "Haanel";
	private static final String HAMLET = "Hamlet";
	private static final String HAWTHORNE = "Hawthorne";
	private static final String HAWKESBURY = "Hawkesbury";
	private static final String HERONGATE = "Herongate";
	private static final String HERTZBERG = "Hertzberg";
	private static final String HILLCREST = "Hillcrest";
	private static final String HILLCREST_HIGH_SCHOOL = HILLCREST + " " + HIGH_SCHOOL;
	private static final String HINES = "Hines";
	private static final String HOSPITAL = "Hospital";
	private static final String HUNTMAR = "Huntmar";
	private static final String HURDMAN = "Hurdman";
	private static final String HURDMAN_STA = HURDMAN + " Sta";
	private static final String IMMACULATA_HIGH_SCHOOL = "Immaculata " + HIGH_SCHOOL;
	private static final String INNOVATION = "Innovation";
	private static final String JEANNE_D_ARC = "Jeanne d'Arc";
	private static final String JEANNE_D_ARC_STA = JEANNE_D_ARC + " Sta";
	private static final String KANATA = "Kanata";
	private static final String KANATA_LAKES = KANATA + " Lks";
	private static final String KARS = "Kars";
	private static final String KATIMAVIK = "Katimavik";
	private static final String KNOXDALE = "Knoxdale";
	private static final String LANSDOWNE = "Lansdowne";
	private static final String LANSDOWNE_PARK = LANSDOWNE + " Pk";
	private static final String LE_BRETON = "LeBreton";
	private static final String LEITRIM = "Leitrim";
	private static final String LESTER_B_PEARSON_HIGH_SCHOOL = "Lester B Pearson " + HIGH_SCHOOL;
	private static final String LINCOLN_FIELDS = "Lincoln Fields";
	private static final String LOCAL = "Local";
	private static final String MACKENZIE_KING = "Mackenzie King";
	private static final String MAIN = "Main";
	private static final String MANOTICK = "Manotick";
	private static final String MC_CARTHY = "McCarthy";
	private static final String MEADOWGLEN = "Meadowglen";
	private static final String MERIVALE = "Merivale";
	private static final String METCALFE = "Metcalfe";
	private static final String MILLENNIUM = "Millennium";
	private static final String MILLENNIUM_STA = MILLENNIUM + " Sta";
	private static final String NAVAN = "Navan";
	private static final String NEPEAN_CTR = "Nepean Ctr";
	private static final String NORTH_GOWER = "North Gower";
	private static final String OMER_DESLAURIER_HIGH_SCHOOL = "Omer-Deslaurier" + " " + HIGH_SCHOOL;
	private static final String ORLEANS = "Orléans";
	private static final String OSGOODE = "Osgoode";
	private static final String OTTAWA = "Ottawa";
	private static final String OTTAWA_ROCKCLIFFE = OTTAWA + "-Rockcliffe";
	private static final String PAGE = "Page";
	private static final String PARLIAMENT = "Parliament";
	private static final String PETRIE_ISL = "Petrie Isl";
	private static final String PINECREST = "Pinecrest";
	private static final String PINEVIEW = "Pineview";
	private static final String PLACE_D_ORLEANS = "Pl d'" + ORLEANS;
	private static final String PORTOBELLO = "Portobello";
	private static final String QUEENSWAY_TER = "Queensway Ter";
	private static final String QUEENSWOOD_HTS = "Queenswood Hts";
	private static final String RENAUD = "Renaud";
	private static final String RICHMOND = "Richmond";
	private static final String RIDEAU = "Rideau";
	private static final String RIDEAU_CTR = RIDEAU + " Ctr";
	private static final String RIDGEMONT = "Ridgemont";
	private static final String RIDGEMONT_HIGH_SCHOOL = RIDGEMONT + " " + HIGH_SCHOOL;
	private static final String RIVERVIEW = "Riverview";
	private static final String SARSFIELD = "Sarsfield";
	private static final String SOUTH_KEYS = "South Keys";
	private static final String SOUTHKEYS = "Southkeys";
	private static final String ST_JOSEPH = "St Joseph";
	private static final String ST_LAURENT = "St Laurent";
	private static final String ST_LAURENT_STA = ST_LAURENT + " Sta";
	private static final String ST_LOUIS = "St-Louis";
	private static final String ST_PATRICK_S = "St. Patrick's";
	private static final String ST_PATRICK_S_HIGH_SCHOOL = ST_PATRICK_S + " " + HIGH_SCHOOL;
	private static final String ST_PIUS_X_HIGH_SCHOOL = "St Pius X " + HIGH_SCHOOL;
	private static final String STITTSVILLE = "Stittsville";
	private static final String SUNVIEW = "Sunview";
	private static final String TANGER = "Tanger";
	private static final String TENTH_LINE = "Tenth Line";
	private static final String TERRY_FOX = "Terry Fox";
	private static final String TERRY_FOX_STA = TERRY_FOX + " Sta";
	private static final String TRIM = "Trim";
	private static final String TUNNEY_S_PASTURE = "Tunney's Pasture";
	private static final String UPLANDS = "Uplands";
	private static final String VAAN = "Vaan";
	private static final String VARS = "Vars";
	private static final String WALKLEY = "Walkley";
	private static final String WATERIDGE = "Wateridge";
	private static final String WOODROFFE = "Woodroffe";
	private static final String YOUVILLE = "Youville";

	private static final String ROUTE_1 = SOUTH_KEYS + RLN_SEP + OTTAWA_ROCKCLIFFE;
	private static final String ROUTE_2 = DOWNTOWN + RLN_SEP + BAYSHORE;
	private static final String ROUTE_4 = HURDMAN + RLN_SEP + RIDEAU_CTR;
	private static final String ROUTE_5 = BILLINGS_BRIDGE + RLN_SEP + ST_LAURENT;
	private static final String ROUTE_6 = HURDMAN + RLN_SEP + TUNNEY_S_PASTURE;
	private static final String ROUTE_7 = CARLETON + RLN_SEP + ST_LAURENT;
	private static final String ROUTE_8 = BILLINGS_BRIDGE + RLN_SEP + GATINEAU;
	private static final String ROUTE_9 = RIDEAU_CTR + RLN_SEP + HURDMAN;
	private static final String ROUTE_10 = CARLETON + RLN_SEP + RIDEAU;
	private static final String ROUTE_11 = DOWNTOWN + RLN_SEP + BAYSHORE;
	private static final String ROUTE_12 = BLAIR + RLN_SEP + RIDEAU_CTR;
	private static final String ROUTE_14 = ST_LAURENT + RLN_SEP + CARLINGTON;
	private static final String ROUTE_15 = BLAIR + RLN_SEP + GATINEAU;
	private static final String ROUTE_16 = MAIN + RLN_SEP + BRITANNIA;
	private static final String ROUTE_17 = WATERIDGE + RLN_SEP + PARLIAMENT;
	private static final String ROUTE_18 = ST_LAURENT + RLN_SEP + RIDEAU_CTR;
	private static final String ROUTE_19 = ST_LAURENT + RLN_SEP + BANK;
	private static final String ROUTE_20 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_21 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_22 = ALBERT + SLASH + BAY + RLN_SEP + MILLENNIUM;
	private static final String ROUTE_23 = BLAIR + RLN_SEP + "Rothwell Hts";
	private static final String ROUTE_24 = ALBERT + SLASH + BAY + RLN_SEP + BEACON_HILL;
	private static final String ROUTE_25 = MILLENNIUM + RLN_SEP + "La Cité";
	private static final String ROUTE_26 = BLAIR + RLN_SEP + PINEVIEW;
	private static final String ROUTE_27 = GATINEAU + RLN_SEP + ORLEANS;
	private static final String ROUTE_28 = BLACKBURN + " " + HAMLET + RLN_SEP + BLAIR;
	private static final String ROUTE_30 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_31 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_32 = SUNVIEW + RLN_SEP + BLAIR;
	private static final String ROUTE_33 = PLACE_D_ORLEANS + COLON + ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_34 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_35 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_37 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_38 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_39 = BLAIR + RLN_SEP + MILLENNIUM;
	private static final String ROUTE_40 = GATINEAU + RLN_SEP + BLOSSOM_PARK;
	private static final String ROUTE_41 = HURDMAN + RLN_SEP + WALKLEY;
	private static final String ROUTE_42 = BLAIR + RLN_SEP + HURDMAN;
	private static final String ROUTE_43 = HURDMAN + RLN_SEP + CONROY;
	private static final String ROUTE_44 = GATINEAU + RLN_SEP + BILLINGS_BRIDGE;
	private static final String ROUTE_45 = HURDMAN + RLN_SEP + HOSPITAL;
	private static final String ROUTE_46 = HURDMAN + RLN_SEP + BILLINGS_BRIDGE;
	private static final String ROUTE_47 = HAWTHORNE + RLN_SEP + ST_LAURENT;
	private static final String ROUTE_48 = ELMVALE + RLN_SEP + BILLINGS_BRIDGE + SLASH + HURDMAN;
	private static final String ROUTE_49 = ELMVALE + RLN_SEP + HURDMAN;
	private static final String ROUTE_50 = TUNNEY_S_PASTURE + RLN_SEP + LINCOLN_FIELDS;
	private static final String ROUTE_51 = TUNNEY_S_PASTURE + RLN_SEP + BRITANNIA;
	private static final String ROUTE_53 = TUNNEY_S_PASTURE + RLN_SEP + CARLINGTON;
	private static final String ROUTE_54 = TUNNEY_S_PASTURE + SLASH + LOCAL;
	private static final String ROUTE_55 = ELMVALE + RLN_SEP + BAYSHORE;
	private static final String ROUTE_56 = HURDMAN + RLN_SEP + TUNNEY_S_PASTURE;
	private static final String ROUTE_57 = TUNNEY_S_PASTURE + RLN_SEP + BELLS_CORNERS;
	private static final String ROUTE_58 = TUNNEY_S_PASTURE + RLN_SEP + "Moodie";
	private static final String ROUTE_60 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_61 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_62 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_63 = LINCOLN_FIELDS + COLON + MACKENZIE_KING + RLN_SEP + INNOVATION;
	private static final String ROUTE_64 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_65 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_66 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_67 = MACKENZIE_KING + RLN_SEP + PINECREST;
	private static final String ROUTE_68 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_69 = MACKENZIE_KING + RLN_SEP + NEPEAN_CTR;
	private static final String ROUTE_70 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_71 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_72 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_73 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_74 = RIVERVIEW + RLN_SEP + TUNNEY_S_PASTURE;
	private static final String ROUTE_75 = BARRHAVEN_CTR + RLN_SEP + TUNNEY_S_PASTURE;
	private static final String ROUTE_77 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_80 = BARRHAVEN_CTR + RLN_SEP + TUNNEY_S_PASTURE;
	private static final String ROUTE_81 = TUNNEY_S_PASTURE + RLN_SEP + CLYDE;
	private static final String ROUTE_82 = LINCOLN_FIELDS + RLN_SEP + BAYSHORE;
	private static final String ROUTE_83 = BASELINE + RLN_SEP + MERIVALE;
	private static final String ROUTE_84 = LINCOLN_FIELDS + SLASH + CTRPOINTE;
	private static final String ROUTE_85 = HURDMAN + RLN_SEP + BAYSHORE;
	private static final String ROUTE_86 = ELMVALE + RLN_SEP + BASELINE + SLASH + COLONNADE;
	private static final String ROUTE_87 = SOUTH_KEYS + RLN_SEP + BASELINE;
	private static final String ROUTE_88 = HURDMAN + RLN_SEP + KANATA;
	private static final String ROUTE_89 = TUNNEY_S_PASTURE + RLN_SEP + COLONNADE;
	private static final String ROUTE_90 = GREENBORO + RLN_SEP + HURDMAN;
	private static final String ROUTE_91 = ORLEANS + AND + TRIM + RLN_SEP + BASELINE;
	private static final String ROUTE_92 = ST_LAURENT + RLN_SEP + TERRY_FOX + AND + STITTSVILLE;
	private static final String ROUTE_93 = LINCOLN_FIELDS + RLN_SEP + KANATA + " North" + SLASH + LE_BRETON;
	private static final String ROUTE_94 = RIVERVIEW + RLN_SEP + MILLENNIUM;
	private static final String ROUTE_95 = ORLEANS + AND + TRIM + RLN_SEP + BARRHAVEN_CTR;
	private static final String ROUTE_96 = BLAIR + ", " + HURDMAN + RLN_SEP + TERRY_FOX + ", " + STITTSVILLE;
	private static final String ROUTE_97 = AIRPORT + RLN_SEP + BAYSHORE + AND + BELLS_CORNERS;
	private static final String ROUTE_98 = HAWTHORNE + RLN_SEP + GREENBORO + AND + TUNNEY_S_PASTURE;
	private static final String ROUTE_99 = GREENBORO + RLN_SEP + BARRHAVEN + SLASH + MANOTICK;
	private static final String ROUTE_101 = ST_LAURENT + RLN_SEP + BAYSHORE;
	private static final String ROUTE_103 = PLACE_D_ORLEANS + RLN_SEP + BAYSHORE;
	private static final String ROUTE_104 = PLACE_D_ORLEANS + RLN_SEP + CARLETON;
	private static final String ROUTE_105 = GATINEAU + RLN_SEP + TUNNEY_S_PASTURE;
	private static final String ROUTE_106 = ELMVALE + RLN_SEP + HURDMAN;
	private static final String ROUTE_107 = BAYVIEW + RLN_SEP + SOUTHKEYS;
	private static final String ROUTE_111 = BILLINGS_BRIDGE + SLASH + CARLETON + RLN_SEP + BASELINE;
	private static final String ROUTE_112 = ELMVALE + RLN_SEP + BILLINGS_BRIDGE;
	private static final String ROUTE_114 = HURDMAN + RLN_SEP + GREENBORO;
	private static final String ROUTE_116 = GREENBORO + AND + HURDMAN + RLN_SEP + MERIVALE;
	private static final String ROUTE_118 = HURDMAN + RLN_SEP + KANATA;
	private static final String ROUTE_120 = PORTOBELLO + RLN_SEP + PLACE_D_ORLEANS;
	private static final String ROUTE_121 = BLAIR + RLN_SEP + HURDMAN;
	private static final String ROUTE_122 = MILLENNIUM + RLN_SEP + PLACE_D_ORLEANS;
	private static final String ROUTE_123 = GLOUCESTER_NORTH + SLASH + BLAIR;
	private static final String ROUTE_124 = BEACON_HILL + RLN_SEP + HURDMAN;
	private static final String ROUTE_126 = PINEVIEW + RLN_SEP + HURDMAN;
	private static final String ROUTE_127 = BLAIR + RLN_SEP + ST_LAURENT;
	private static final String ROUTE_128 = BLACKBURN + " " + HAMLET + RLN_SEP + HURDMAN;
	private static final String ROUTE_129 = CARSON_S + RLN_SEP + HURDMAN;
	private static final String ROUTE_130 = MILLENNIUM + RLN_SEP + BLAIR;
	private static final String ROUTE_131 = CONVENT_GLN + RLN_SEP + CHAPEL_HL;
	private static final String ROUTE_132 = PLACE_D_ORLEANS + RLN_SEP + SUNVIEW;
	private static final String ROUTE_134 = RENAUD + RLN_SEP + PLACE_D_ORLEANS;
	private static final String ROUTE_135 = ESPRIT + RLN_SEP + PLACE_D_ORLEANS;
	private static final String ROUTE_136 = TENTH_LINE + RLN_SEP + PLACE_D_ORLEANS;
	private static final String ROUTE_137 = QUEENSWOOD_HTS + SLASH + PLACE_D_ORLEANS;
	private static final String ROUTE_138 = ST_LOUIS + RLN_SEP + PLACE_D_ORLEANS;
	private static final String ROUTE_139 = PETRIE_ISL + RLN_SEP + PLACE_D_ORLEANS;
	private static final String ROUTE_140 = MC_CARTHY + RLN_SEP + HURDMAN;
	private static final String ROUTE_141 = "Kaladar";
	private static final String ROUTE_143 = CONROY + RLN_SEP + SOUTH_KEYS;
	private static final String ROUTE_144 = LEITRIM + RLN_SEP + SOUTH_KEYS;
	private static final String ROUTE_146 = SOUTH_KEYS + RLN_SEP + HURDMAN;
	private static final String ROUTE_147 = UPLANDS + SLASH + SOUTH_KEYS;
	private static final String ROUTE_148 = ELMVALE + RLN_SEP + HURDMAN;
	private static final String ROUTE_149 = ELMVALE + RLN_SEP + HURDMAN;
	private static final String ROUTE_150 = TUNNEY_S_PASTURE + RLN_SEP + LINCOLN_FIELDS;
	private static final String ROUTE_151 = TUNNEY_S_PASTURE + RLN_SEP + CLYDE;
	private static final String ROUTE_152 = LINCOLN_FIELDS + RLN_SEP + "Moodie" + AND + BAYSHORE;
	private static final String ROUTE_153 = CARLINGWOOD + RLN_SEP + LINCOLN_FIELDS;
	private static final String ROUTE_154 = QUEENSWAY_TER + RLN_SEP + LINCOLN_FIELDS;
	private static final String ROUTE_155 = QUEENSWAY_TER + RLN_SEP + PINECREST + SLASH + BAYSHORE;
	private static final String ROUTE_156 = BASELINE + RLN_SEP + MERIVALE;
	private static final String ROUTE_157 = BASELINE + RLN_SEP + AMBERWOOD;
	private static final String ROUTE_158 = BAYSHORE + RLN_SEP + HAANEL;
	private static final String ROUTE_159 = TUNNEY_S_PASTURE + RLN_SEP + LOCAL;
	private static final String ROUTE_161 = BRIDLEWOOD + RLN_SEP + TERRY_FOX;
	private static final String ROUTE_162 = TERRY_FOX + RLN_SEP + STITTSVILLE;
	private static final String ROUTE_164 = BRIDLEWOOD + RLN_SEP + TERRY_FOX;
	private static final String ROUTE_165 = INNOVATION + RLN_SEP + TERRY_FOX;
	private static final String ROUTE_166 = INNOVATION + RLN_SEP + EAGLESON;
	private static final String ROUTE_167 = TERRY_FOX + RLN_SEP + "Blackstone";
	private static final String ROUTE_168 = BEAVERBROOK + RLN_SEP + KATIMAVIK;
	private static final String ROUTE_170 = FALLOWFIELD + RLN_SEP + BARRHAVEN_CTR;
	private static final String ROUTE_171 = FALLOWFIELD + RLN_SEP + BARRHAVEN_CTR;
	private static final String ROUTE_172 = LINCOLN_FIELDS + RLN_SEP + BAYSHORE;
	private static final String ROUTE_173 = BARRHAVEN_CTR + RLN_SEP + FALLOWFIELD + AND + BAYSHORE;
	private static final String ROUTE_174 = BASELINE + RLN_SEP + KNOXDALE;
	private static final String ROUTE_175 = GOLFLINKS + RLN_SEP + BARRHAVEN_CTR;
	private static final String ROUTE_176 = BARRHAVEN_CTR + RLN_SEP + TUNNEY_S_PASTURE;
	private static final String ROUTE_177 = BARRHAVEN_CTR + SLASH + FALLOWFIELD + RLN_SEP + CAMBRIAN;
	private static final String ROUTE_178 = LINCOLN_FIELDS + SLASH + CTRPOINTE;
	private static final String ROUTE_179 = CITIGATE + RLN_SEP + FALLOWFIELD;
	private static final String ROUTE_180 = BAYSHORE + RLN_SEP + HAANEL;
	private static final String ROUTE_181 = EAGLESON + RLN_SEP + HERTZBERG;
	private static final String ROUTE_182 = KANATA + RLN_SEP + LINCOLN_FIELDS;
	private static final String ROUTE_185 = LE_BRETON + SLASH + EXPERIMENTAL_FARM;
	private static final String ROUTE_186 = MANOTICK + RLN_SEP + BARRHAVEN_CTR;
	private static final String ROUTE_187 = BASELINE + RLN_SEP + AMBERWOOD;
	private static final String ROUTE_188 = CANADIAN_TIRE_CTR + RLN_SEP + HUNTMAR;
	private static final String ROUTE_189 = RIVERVIEW + RLN_SEP + GREENBORO;
	private static final String ROUTE_190 = HURDMAN + RLN_SEP + "Mooney's Bay";
	private static final String ROUTE_192 = HAWTHORNE + RLN_SEP + HURDMAN;
	private static final String ROUTE_193 = PLACE_D_ORLEANS + RLN_SEP + BLAIR;
	private static final String ROUTE_194 = GLOUCESTER_NORTH + RLN_SEP + BLAIR;
	private static final String ROUTE_196 = TANGER + RLN_SEP + TERRY_FOX;
	private static final String ROUTE_197 = UPLANDS + SLASH + GREENBORO;
	private static final String ROUTE_198 = PETRIE_ISL + RLN_SEP + PLACE_D_ORLEANS;
	private static final String ROUTE_199 = BARRHAVEN + RLN_SEP + PLACE_D_ORLEANS;
	private static final String ROUTE_201 = BAYSHORE + SLASH + CARLINGWOOD + RLN_SEP + STITTSVILLE + SLASH + RICHMOND;
	private static final String ROUTE_202 = PLACE_D_ORLEANS + SLASH + ST_LAURENT + RLN_SEP + NAVAN + SLASH + SARSFIELD + SLASH + CUMBERLAND;
	private static final String ROUTE_203 = BAYSHORE + ", " + CARLINGWOOD + RLN_SEP + STITTSVILLE + ", " + DUNROBIN + ", " + CARP;
	private static final String ROUTE_204 = SOUTH_KEYS + SLASH + BILLINGS_BRIDGE + RLN_SEP + GREELY + SLASH + METCALFE;
	private static final String ROUTE_205 = BARRHAVEN + SLASH + CARLINGWOOD + RLN_SEP + MANOTICK + SLASH + KARS + SLASH + NORTH_GOWER;
	private static final String ROUTE_221 = ALBERT + SLASH + BAY + RLN_SEP + CUMBERLAND;
	private static final String ROUTE_222 = ALBERT + SLASH + BAY + RLN_SEP + VARS;
	private static final String ROUTE_224 = ALBERT + SLASH + BAY + RLN_SEP + BEACON_HILL;
	private static final String ROUTE_225 = BLAIR + RLN_SEP + RENAUD;
	private static final String ROUTE_228 = ALBERT + SLASH + BAY + RLN_SEP + NAVAN;
	private static final String ROUTE_231 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_232 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_233 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_234 = GATINEAU + RLN_SEP + ORLEANS;
	private static final String ROUTE_235 = ALBERT + SLASH + BAY + RLN_SEP + GARDENWAY;
	private static final String ROUTE_236 = ALBERT + SLASH + BAY + RLN_SEP + ESPRIT;
	private static final String ROUTE_237 = ALBERT + SLASH + BANK + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_251 = TUNNEY_S_PASTURE + RLN_SEP + BELLS_CORNERS;
	private static final String ROUTE_252 = MACKENZIE_KING + RLN_SEP + BELLS_CORNERS;
	private static final String ROUTE_256 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_257 = BRIDLEWOOD + RLN_SEP + MACKENZIE_KING;
	private static final String ROUTE_258 = GRANDVIEW + RLN_SEP + TUNNEY_S_PASTURE;
	private static final String ROUTE_261 = MACKENZIE_KING + RLN_SEP + STITTSVILLE + ", Main";
	private static final String ROUTE_262 = MACKENZIE_KING + RLN_SEP + "West Ridge";
	private static final String ROUTE_263 = MACKENZIE_KING + RLN_SEP + "Stanley Corner";
	private static final String ROUTE_264 = MACKENZIE_KING + RLN_SEP + TERRY_FOX;
	private static final String ROUTE_265 = MACKENZIE_KING + RLN_SEP + BEAVERBROOK;
	private static final String ROUTE_266 = TUNNEY_S_PASTURE + RLN_SEP + "Maxwell Bridge";
	private static final String ROUTE_267 = MACKENZIE_KING + RLN_SEP + "Glen Cairn";
	private static final String ROUTE_268 = MACKENZIE_KING + RLN_SEP + KANATA_LAKES;
	private static final String ROUTE_269 = MACKENZIE_KING + RLN_SEP + BRIDLEWOOD;
	private static final String ROUTE_270 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_271 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_272 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_273 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_275 = MACKENZIE_KING + RLN_SEP + CAMBRIAN;
	private static final String ROUTE_277 = MACKENZIE_KING + RLN_SEP + "Nepean Woods";
	private static final String ROUTE_278 = MACKENZIE_KING + RLN_SEP + "Riverside South";
	private static final String ROUTE_282 = MACKENZIE_KING + RLN_SEP + PINECREST;
	private static final String ROUTE_283 = MACKENZIE_KING + RLN_SEP + RICHMOND;
	private static final String ROUTE_284 = TUNNEY_S_PASTURE + RLN_SEP + KNOXDALE;
	private static final String ROUTE_290 = MC_CARTHY + RLN_SEP + HURDMAN;
	private static final String ROUTE_291 = HURDMAN + RLN_SEP + HERONGATE;
	private static final String ROUTE_293 = GATINEAU + RLN_SEP + BLOSSOM_PARK;
	private static final String ROUTE_294 = HURDMAN + RLN_SEP + "Findlay Creek";
	private static final String ROUTE_299 = LE_BRETON + RLN_SEP + MANOTICK;
	private static final String ROUTE_298 = HURDMAN + RLN_SEP + CONROY;
	private static final String ROUTE_301 = CARLINGWOOD + RLN_SEP + RICHMOND + SLASH + STITTSVILLE;
	private static final String ROUTE_302 = ST_LAURENT + RLN_SEP + CUMBERLAND + SLASH + SARSFIELD + SLASH + NAVAN;
	private static final String ROUTE_303 = CARLINGWOOD + RLN_SEP + DUNROBIN + SLASH + CARP;
	private static final String ROUTE_304 = BILLINGS_BRIDGE + RLN_SEP + METCALFE + SLASH + GREELY + SLASH + OSGOODE;
	private static final String ROUTE_305 = CARLINGWOOD + RLN_SEP + MANOTICK + SLASH + NORTH_GOWER + SLASH + KARS;
	private static final String ROUTE_401 = CANADIAN_TIRE_CTR;
	private static final String ROUTE_402 = CANADIAN_TIRE_CTR;
	private static final String ROUTE_403 = CANADIAN_TIRE_CTR;
	private static final String ROUTE_404 = CANADIAN_TIRE_CTR;
	private static final String ROUTE_405 = CANADIAN_TIRE_CTR;
	private static final String ROUTE_406 = CANADIAN_TIRE_CTR;
	private static final String ROUTE_450 = LANSDOWNE + RLN_SEP + RIDEAU_CTR;
	private static final String ROUTE_451 = LANSDOWNE_PARK + RLN_SEP + BLAIR;
	private static final String ROUTE_452 = LANSDOWNE_PARK + RLN_SEP + SOUTH_KEYS;
	private static final String ROUTE_454 = LANSDOWNE + RLN_SEP + TERRY_FOX;
	private static final String ROUTE_455 = LANSDOWNE + RLN_SEP + TRIM;
	private static final String ROUTE_456 = LANSDOWNE_PARK + RLN_SEP + BARRHAVEN_CTR;
	private static final String ROUTE_505 = Constants.EMPTY; // TODO
	private static final String ROUTE_506 = Constants.EMPTY; // TODO
	private static final String ROUTE_520 = HAWKESBURY + RLN_SEP + OTTAWA + RLN_SEP + GATINEAU;
	private static final String ROUTE_555 = "Casselman" + RLN_SEP + OTTAWA + RLN_SEP + GATINEAU;
	private static final String ROUTE_602 = E_S_DE_LA_SALLE + RLN_SEP + HURDMAN;
	private static final String ROUTE_609 = OTTAWA + " Technical " + " S.S." + RLN_SEP + ST_LAURENT;
	private static final String ROUTE_611 = E_S_GISELE_LALONDE + RLN_SEP + CHAPEL_HL;
	private static final String ROUTE_612 = E_S_GISELE_LALONDE + RLN_SEP + CHAPEL_HL;
	private static final String ROUTE_613 = IMMACULATA_HIGH_SCHOOL + RLN_SEP + HURDMAN_STA;
	private static final String ROUTE_618 = E_S_LOUIS_RIEL + RLN_SEP + MILLENNIUM_STA;
	private static final String ROUTE_619 = E_S_LOUIS_RIEL + RLN_SEP + BLAIR_STA;
	private static final String ROUTE_620 = OTTAWA + " Technical " + " S.S." + RLN_SEP + ST_LAURENT;
	private static final String ROUTE_622 = COLONEL_BY_HIGH_SCHOOL + RLN_SEP + BLACKBURN + SLASH + PAGE;
	private static final String ROUTE_624 = GLOUCESTER_HIGH_SCHOOL + RLN_SEP + RIDEAU;
	private static final String ROUTE_630 = COLONEL_BY_HIGH_SCHOOL + RLN_SEP + MILLENNIUM;
	private static final String ROUTE_631 = COLONEL_BY + SLASH + GLOUCESTER_HIGH_SCHOOL + RLN_SEP + CHAPEL_HL;
	private static final String ROUTE_632 = E_S_GISELE_LALONDE + RLN_SEP + QUEENSWOOD_HTS;
	private static final String ROUTE_633 = LESTER_B_PEARSON_HIGH_SCHOOL + RLN_SEP + ST_LAURENT_STA;
	private static final String ROUTE_634 = PLACE_D_ORLEANS + RLN_SEP + "Collège Catholique Mer Bleue";
	private static final String ROUTE_635 = CAIRINE_WILSON_HIGH_SCHOOL + RLN_SEP + ORLEANS;
	private static final String ROUTE_636 = PLACE_D_ORLEANS + RLN_SEP + "Sir Wilfrid Laurier SS";
	private static final String ROUTE_638 = ORLEANS;
	private static final String ROUTE_639 = PLACE_D_ORLEANS + RLN_SEP + "Gisèle Lalonde";
	private static final String ROUTE_640 = BROOKFIELD_HIGH_SCHOOL + RLN_SEP + GREENBORO_STA;
	private static final String ROUTE_641 = E_S_LOUIS_RIEL + RLN_SEP + MEADOWGLEN + SLASH + ORLEANS;
	private static final String ROUTE_644 = CANTERBURY_HIGH_SCHOOL + RLN_SEP + GREENBORO;
	private static final String ROUTE_645 = HURDMAN;
	private static final String ROUTE_648 = E_S_LOUIS_RIEL + RLN_SEP + YOUVILLE + SLASH + ST_JOSEPH;
	private static final String ROUTE_649 = HILLCREST_HIGH_SCHOOL + RLN_SEP + GREENBORO;
	private static final String ROUTE_658 = BELL_HIGH_SCHOOL + RLN_SEP + GRANDVIEW;
	private static final String ROUTE_660 = BELL_HIGH_SCHOOL + RLN_SEP + INNOVATION;
	private static final String ROUTE_661 = BELL_HIGH_SCHOOL + RLN_SEP + TERRY_FOX_STA;
	private static final String ROUTE_665 = BELL_HIGH_SCHOOL + RLN_SEP + BRIDLEWOOD;
	private static final String ROUTE_669 = BELL_HIGH_SCHOOL + RLN_SEP + BAYSHORE + SLASH + CARLING;
	private static final String ROUTE_670 = ST_PIUS_X_HIGH_SCHOOL + RLN_SEP + VAAN + SLASH + WOODROFFE;
	private static final String ROUTE_674 = ALL_STS_CATHOLIC_AND_STEPHEN_LEACOCK_SCHOOLS + RLN_SEP + INNOVATION + SLASH + HINES;
	private static final String ROUTE_675 = BELL_HIGH_SCHOOL + RLN_SEP + "Minto Rec";
	private static final String ROUTE_678 = E_S_LOUIS_RIEL + RLN_SEP + JEANNE_D_ARC_STA;
	private static final String ROUTE_681 = BELL_HIGH_SCHOOL + RLN_SEP + BRIDLEWOOD;
	private static final String ROUTE_686 = OMER_DESLAURIER_HIGH_SCHOOL + RLN_SEP + BASELINE;
	private static final String ROUTE_689 = OMER_DESLAURIER_HIGH_SCHOOL + RLN_SEP + BILLINGS_BRIDGE;
	private static final String ROUTE_691 = E_S_DESLAURIERS + RLN_SEP + BAYSHORE_STA;
	private static final String ROUTE_698 = RIDGEMONT_HIGH_SCHOOL + SLASH + ST_PATRICK_S_HIGH_SCHOOL + RLN_SEP + GREENBORO;
	private static final String ROUTE_696 = BASELINE + RLN_SEP + GREENBORO;
	private static final String ROUTE_701 = BLAIR + RLN_SEP + TUNNEY_S_PASTURE;
	private static final String ROUTE_702 = BAYVIEW + RLN_SEP + SOUTH_KEYS;

	@NotNull
	@Override
	public String getRouteLongName(@NotNull GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteLongName())) {
			if (!Utils.isDigitsOnly(gRoute.getRouteShortName())) {
				if ("R1".equalsIgnoreCase(gRoute.getRouteShortName())) { // 701
					return ROUTE_701;
				} else if ("Hurd".equalsIgnoreCase(gRoute.getRouteShortName())) { // 104
					return ROUTE_104;
				}
			} else {
				int rsn = Integer.parseInt(gRoute.getRouteShortName());
				switch (rsn) {
				// @formatter:off
				case 1: return ROUTE_1;
				case 2: return ROUTE_2;
				case 4: return ROUTE_4;
				case 5: return ROUTE_5;
				case 6: return ROUTE_6;
				case 7: return ROUTE_7;
				case 8: return ROUTE_8;
				case 9: return ROUTE_9;
				case 10: return ROUTE_10;
				case 11: return ROUTE_11;
				case 12: return ROUTE_12;
				case 14: return ROUTE_14;
				case 15: return ROUTE_15;
				case 16: return ROUTE_16;
				case 17: return ROUTE_17;
				case 18: return ROUTE_18;
				case 19: return ROUTE_19;
				case 20: return ROUTE_20;
				case 21: return ROUTE_21;
				case 22: return ROUTE_22;
				case 23: return ROUTE_23;
				case 24: return ROUTE_24;
				case 25: return ROUTE_25;
				case 26: return ROUTE_26;
				case 27: return ROUTE_27;
				case 28: return ROUTE_28;
				case 30: return ROUTE_30;
				case 31: return ROUTE_31;
				case 32: return ROUTE_32;
				case 33: return ROUTE_33;
				case 34: return ROUTE_34;
				case 35: return ROUTE_35;
				case 37: return ROUTE_37;
				case 38: return ROUTE_38;
				case 39: return ROUTE_39;
				case 40: return ROUTE_40;
				case 41: return ROUTE_41;
				case 42: return ROUTE_42;
				case 43: return ROUTE_43;
				case 44: return ROUTE_44;
				case 45: return ROUTE_45;
				case 46: return ROUTE_46;
				case 47: return ROUTE_47;
				case 48: return ROUTE_48;
				case 49: return ROUTE_49;
				case 50: return ROUTE_50;
				case 51: return ROUTE_51;
				case 53: return ROUTE_53;
				case 54: return ROUTE_54;
				case 55: return ROUTE_55;
				case 56: return ROUTE_56;
				case 57: return ROUTE_57;
				case 58: return ROUTE_58;
				case 60: return ROUTE_60;
				case 61: return ROUTE_61;
				case 62: return ROUTE_62;
				case 63: return ROUTE_63;
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
				case 74: return ROUTE_74;
				case 75: return ROUTE_75;
				case 77: return ROUTE_77;
				case 80: return ROUTE_80;
				case 81: return ROUTE_81;
				case 82: return ROUTE_82;
				case 83: return ROUTE_83;
				case 84: return ROUTE_84;
				case 85: return ROUTE_85;
				case 86: return ROUTE_86;
				case 87: return ROUTE_87;
				case 88: return ROUTE_88;
				case 89: return ROUTE_89;
				case 90: return ROUTE_90;
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
				case 103: return ROUTE_103;
				case 104: return ROUTE_104;
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
				case 138: return ROUTE_138;
				case 139: return ROUTE_139;
				case 140: return ROUTE_140;
				case 141: return ROUTE_141;
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
				case 158: return ROUTE_158;
				case 159: return ROUTE_159;
				case 161: return ROUTE_161;
				case 162: return ROUTE_162;
				case 164: return ROUTE_164;
				case 165: return ROUTE_165;
				case 166: return ROUTE_166;
				case 167: return ROUTE_167;
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
				case 179: return ROUTE_179;
				case 180: return ROUTE_180;
				case 181: return ROUTE_181;
				case 182: return ROUTE_182;
				case 185: return ROUTE_185;
				case 186: return ROUTE_186;
				case 187: return ROUTE_187;
				case 188: return ROUTE_188;
				case 189: return ROUTE_189;
				case 190: return ROUTE_190;
				case 192: return ROUTE_192;
				case 193: return ROUTE_193;
				case 194: return ROUTE_194;
				case 196: return ROUTE_196;
				case 197: return ROUTE_197;
				case 198: return ROUTE_198;
				case 199: return ROUTE_199;
				case 201: return ROUTE_201;
				case 202: return ROUTE_202;
				case 203: return ROUTE_203;
				case 204: return ROUTE_204;
				case 205: return ROUTE_205;
				case 221: return ROUTE_221;
				case 222: return ROUTE_222;
				case 224: return ROUTE_224;
				case 225: return ROUTE_225;
				case 228: return ROUTE_228;
				case 231: return ROUTE_231;
				case 232: return ROUTE_232;
				case 233: return ROUTE_233;
				case 234: return ROUTE_234;
				case 235: return ROUTE_235;
				case 236: return ROUTE_236;
				case 237: return ROUTE_237;
				case 251: return ROUTE_251;
				case 252: return ROUTE_252;
				case 256: return ROUTE_256;
				case 257: return ROUTE_257;
				case 258: return ROUTE_258;
				case 261: return ROUTE_261;
				case 262: return ROUTE_262;
				case 263: return ROUTE_263;
				case 264: return ROUTE_264;
				case 265: return ROUTE_265;
				case 266: return ROUTE_266;
				case 267: return ROUTE_267;
				case 268: return ROUTE_268;
				case 269: return ROUTE_269;
				case 270: return ROUTE_270;
				case 271: return ROUTE_271;
				case 272: return ROUTE_272;
				case 273: return ROUTE_273;
				case 275: return ROUTE_275;
				case 277: return ROUTE_277;
				case 278: return ROUTE_278;
				case 282: return ROUTE_282;
				case 283: return ROUTE_283;
				case 284: return ROUTE_284;
				case 290: return ROUTE_290;
				case 291: return ROUTE_291;
				case 293: return ROUTE_293;
				case 294: return ROUTE_294;
				case 298: return ROUTE_298;
				case 299: return ROUTE_299;
				case 301: return ROUTE_301;
				case 302: return ROUTE_302;
				case 303: return ROUTE_303;
				case 304: return ROUTE_304;
				case 305: return ROUTE_305;
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
				case 505: return ROUTE_505;
				case 506: return ROUTE_506;
				case 520: return ROUTE_520;
				case 555: return ROUTE_555;
				case 602: return ROUTE_602;
				case 609: return ROUTE_609;
				case 611: return ROUTE_611;
				case 612: return ROUTE_612;
				case 613: return ROUTE_613;
				case 618: return ROUTE_618;
				case 619: return ROUTE_619;
				case 620: return ROUTE_620;
				case 622: return ROUTE_622;
				case 624: return ROUTE_624;
				case 630: return ROUTE_630;
				case 631: return ROUTE_631;
				case 632: return ROUTE_632;
				case 633: return ROUTE_633;
				case 634: return ROUTE_634;
				case 635: return ROUTE_635;
				case 636: return ROUTE_636;
				case 638: return ROUTE_638;
				case 639: return ROUTE_639;
				case 640: return ROUTE_640;
				case 641: return ROUTE_641;
				case 644: return ROUTE_644;
				case 645: return ROUTE_645;
				case 648: return ROUTE_648;
				case 649: return ROUTE_649;
				case 658: return ROUTE_658;
				case 660: return ROUTE_660;
				case 661: return ROUTE_661;
				case 665: return ROUTE_665;
				case 669: return ROUTE_669;
				case 670: return ROUTE_670;
				case 674: return ROUTE_674;
				case 675: return ROUTE_675;
				case 678: return ROUTE_678;
				case 681: return ROUTE_681;
				case 686: return ROUTE_686;
				case 689: return ROUTE_689;
				case 691: return ROUTE_691;
				case 698: return ROUTE_698;
				case 696: return ROUTE_696;
				case 701: return ROUTE_701;
				case 702: return ROUTE_702;
				// @formatter:on
				}
			}
			throw new MTLog.Fatal("Unexpected route long name for '%s'!", gRoute.toStringPlus());
		}
		return super.getRouteLongName(gRoute);
	}

	private static final String AGENCY_COLOR = "A2211F";

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String ROUTE_COLOR_BLACK = "231F20";
	// TODO ? @Deprecated
	private static final String ROUTE_COLOR_GRAY = "5A5758";
	private static final String ROUTE_COLOR_ORANGE = "A33F26";
	private static final String ROUTE_COLOR_RED = "B31B18";
	private static final String ROUTE_COLOR_GREEN = "396027";
	// TODO ? @Deprecated
	private static final String ROUTE_COLOR_ORCHID = "968472";
	private static final String ROUTE_COLOR_DARK_ORCHID = "968472";
	private static final String ROUTE_COLOR_CORAL = "FF7F50";
	// TODO ? @Deprecated
	private static final String ROUTE_COLOR_DARK_RED = "8B0000";

	private static final String ROUTE_COLOR_RAPID_BLUE_DARK = "293D9B";
	@SuppressWarnings("unused")
	private static final String ROUTE_COLOR_RAPID_BLUE_LIGHT = "3871C2";

	private static final String ROUTE_COLOR_FREQUENT_ORANGE_DARK = "F14623";
	@SuppressWarnings("unused")
	private static final String ROUTE_COLOR_FREQUENT_ORANGE_LIGHT = "F68712";

	private static final String ROUTE_COLOR_LOCAL_GRAY_DARK = "4F4C4C";
	private static final String ROUTE_COLOR_LOCAL_GRAY_LIGHT = "7B7979";

	private static final String ROUTE_COLOR_CONNEXION_PURPLE_DARK = "8D188F";
	@SuppressWarnings("unused")
	private static final String ROUTE_COLOR_CONNEXION_PURPLE_LIGHT = "5D2491";

	private static final String ROUTE_COLOR_SHOPPER = null;
	private static final String ROUTE_COLOR_EVENT = null;
	private static final String ROUTE_COLOR_RURAL_PARTNERS = null;
	private static final String ROUTE_COLOR_SCHOOL = "FFD800"; // School bus yellow

	private static final Collection<Integer> BLACK_ROUTES = Arrays.asList(//
			1, 2, 4, 5, 7, 8, 9, 12, 14, 16, 18, 19, 33, //
			63, 85, 86, 87, 91, 92, 93, 94, 95, 96, 97, 98, 99, //
			101, 103, 104, 106, 107, 111, 112, 114, 116, 118, 120, 121, 122, 123, 124, //
			126, 127, 128, 129, 130, 131, 132, 134, 135, 137, 143, 144, 146, 147, 148, 149, //
			150, 151, 152, 153, 154, 156, 159, 161, 162, 164, 165, 166, 167, 168, 170, 171, 172, 173, 174, //
			175, 176, 177, 178, 185, 196, 198, //
			222, 224, 233, 234, 235, 237, //
			252, 256, 264, 265, 267, 268, 269, //
			270, 271, 272, 273, 277, 282, 290, 293, 298, //
			301, 302, 303, 304, 305 //
	);

	private static final Collection<Integer> ORANGE_ROUTES = Arrays.asList(//
			201, 202, 203, 204, 205 //
	);

	// TODO ? @Deprecated
	private static final Collection<Integer> GRAY_ROUTES = Arrays.asList(//
			91, //
			120, 123, 132, 137, //
			154, 161, 162, 165, 174, 175, 178 //
	);

	private static final Collection<Integer> RED_ROUTES = Arrays.asList(//
			6, 24, 40, 41, 43, 67, //
			105, 136, 140, 155, 157, 180, 181, 182, 186, 188, 189, 192, 193, 194, 199, //
			201, 202, 203, 204, 205 //
	);

	private static final Collection<Integer> GREEN_ROUTES = Arrays.asList(//
			20, 21, 22, 27, 30, 31, 34, 35, 37, 38, //
			60, 61, 62, 64, 65, 66, 68, 69, 70, 71, 72, 73, 77, //
			221, 228, 231, 232, //
			261, 262, 263, 283 //
	);

	private static final Collection<Integer> CTC_C400_ROUTES = Arrays.asList(//
			401, 402, 403, 404, 405, 406 //
	);

	// TODO ? @Deprecated
	private static final Collection<Integer> TDP_ROUTES = Arrays.asList(//
			450, 451, 452, 454, 455, 456 //
	);

	// TODO ? @Deprecated
	private static final Collection<Integer> RP_ROUTES = Arrays.asList(//
			500, 502, 503, 505, 506, 509, 515, 520, 523, 524, 525, 526, 530, 535, 538, 541, 542, 543, //
			551, 552, 553, 555, 556, 557, 558, 559, 565 //
	);

	private static final Collection<Integer> SCHOOL_ROUTES = Arrays.asList(//
			602, 611, 612, 613, 618, 619, 622, 630, 632, 633, 640, 641, 644, 648, 649, //
			660, 661, 665, 669, 670, 674, 678, 681, 691, 698 //
	);

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String getRouteColor(@NotNull GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			if (!Utils.isDigitsOnly(gRoute.getRouteShortName())) {
				if ("R1".equalsIgnoreCase(gRoute.getRouteShortName())) { // 701
					return null;
				} else if ("Hurd".equalsIgnoreCase(gRoute.getRouteShortName())) { // 104
					return ROUTE_COLOR_LOCAL_GRAY_DARK;
				}
			} else {
				int rsn = Integer.parseInt(gRoute.getRouteShortName());
				switch (rsn) {
				// @formatter:off
				case 11: return ROUTE_COLOR_FREQUENT_ORANGE_DARK;
				case 50: return ROUTE_COLOR_LOCAL_GRAY_DARK;
				case 56: return ROUTE_COLOR_LOCAL_GRAY_LIGHT;
				case 61: return ROUTE_COLOR_RAPID_BLUE_DARK;
				case 62: return ROUTE_COLOR_RAPID_BLUE_DARK;
				case 66: return ROUTE_COLOR_LOCAL_GRAY_LIGHT;
				case 80: return ROUTE_COLOR_FREQUENT_ORANGE_DARK;
				case 81: return ROUTE_COLOR_LOCAL_GRAY_DARK;
				case 82: return ROUTE_COLOR_LOCAL_GRAY_DARK;
				case 83: return ROUTE_COLOR_LOCAL_GRAY_DARK;
				case 84: return ROUTE_COLOR_LOCAL_GRAY_DARK;
				case 88: return ROUTE_COLOR_FREQUENT_ORANGE_DARK;
				case 158: return ROUTE_COLOR_LOCAL_GRAY_LIGHT;
				case 187: return ROUTE_COLOR_LOCAL_GRAY_LIGHT;
				case 970: return null;
				case 975: return null;
				// @formatter:on
				}
				if (100 <= rsn && rsn <= 199) {
					return ROUTE_COLOR_LOCAL_GRAY_DARK;
				}
				if (200 <= rsn && rsn <= 299) {
					return ROUTE_COLOR_CONNEXION_PURPLE_DARK;
				}
				if (300 <= rsn && rsn <= 399) {
					return ROUTE_COLOR_SHOPPER;
				}
				if (400 <= rsn && rsn <= 499) {
					return ROUTE_COLOR_EVENT;
				}
				if (500 <= rsn && rsn <= 599) {
					return ROUTE_COLOR_RURAL_PARTNERS;
				}
				if (600 <= rsn && rsn <= 699) {
					return ROUTE_COLOR_SCHOOL;
				}
				// @formatter:off
				if (SCHOOL_ROUTES.contains(rsn)) { return ROUTE_COLOR_CORAL; }
				else if (CTC_C400_ROUTES.contains(rsn)) { return ROUTE_COLOR_DARK_ORCHID; }
				else if (GREEN_ROUTES.contains(rsn)) { return ROUTE_COLOR_GREEN; }
				else if (ORANGE_ROUTES.contains(rsn)) { return ROUTE_COLOR_ORANGE; }
				else if (RED_ROUTES.contains(rsn)) { return ROUTE_COLOR_RED; }
				else if (BLACK_ROUTES.contains(rsn)) { return ROUTE_COLOR_BLACK; }
				else if (GRAY_ROUTES.contains(rsn)) { return ROUTE_COLOR_GRAY; }
				else if (RP_ROUTES.contains(rsn)) { return ROUTE_COLOR_DARK_RED; }
				else if (TDP_ROUTES.contains(rsn)) { return ROUTE_COLOR_ORCHID; }
				// @formatter:on
			}
			if (isGoodEnoughAccepted()) {
				return null;
			}
			MTLog.logFatal("No route color for '%s'!", gRoute);
			return null;
		}
		return super.getRouteColor(gRoute);
	}

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;

	static {
		//noinspection UnnecessaryLocalVariable
		HashMap<Long, RouteTripSpec> map2 = new HashMap<>();
		//noinspection deprecation
		map2.put(190L, new RouteTripSpec(190L, //
				0, MTrip.HEADSIGN_TYPE_STRING, "Mooney's Bay", //
				1, MTrip.HEADSIGN_TYPE_STRING, "Hurdman") //
				.addTripSort(0, //
						Arrays.asList( //
								"AF930", // "3023", // HURDMAN C
								"RB481", // ++
								"RB070" // "1055" // ST. PATRICK'S HOME
						)) //
				.addTripSort(1, //
						Arrays.asList( //
								"RB070", //"1055", // ST. PATRICK'S HOME
								"RA510", // ++
								"AF920" // "3023" // HURDMAN B
						)) //
				.compileBothTripSort());
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId,
							@NotNull List<MTripStop> list1, @NotNull List<MTripStop> list2,
							@NotNull MTripStop ts1, @NotNull MTripStop ts2,
							@NotNull GStop ts1GStop, @NotNull GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop, this);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@NotNull
	@Override
	public ArrayList<MTrip> splitTrip(@NotNull MRoute mRoute, @Nullable GTrip gTrip, @NotNull GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	@NotNull
	@Override
	public Pair<Long[], Integer[]> splitTripStop(@NotNull MRoute mRoute,
												 @NotNull GTrip gTrip,
												 @NotNull GTripStop gTripStop,
												 @NotNull ArrayList<MTrip> splitTrips,
												 @NotNull GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()), this);
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(@NotNull MRoute mRoute, @NotNull MTrip mTrip, @NotNull GTrip gTrip, @NotNull GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		final int directionId = gTrip.getDirectionId() == null ? 0 : gTrip.getDirectionId();
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), directionId);
	}

	private static final Pattern STARTS_WITH_TO_VERS = Pattern.compile("((^.* |^)(to/vers|to / vers))", Pattern.CASE_INSENSITIVE);

	private static final Pattern CAIRINE_WILSON_ = CleanUtils.cleanWords("carine wilson");
	private static final String CAIRINE_WILSON_REPLACEMENT = CleanUtils.cleanWordsReplacement("Cairine Wilson");

	private static final Pattern SARSFIELD_ = CleanUtils.cleanWords("sarfield");
	private static final String SARSFIELD_REPLACEMENT = CleanUtils.cleanWordsReplacement("Sarsfield");

	private static final Pattern ST_LAURENT_ = CleanUtils.cleanWords("st- laurent", "st laurent");
	private static final String ST_LAURENT_REPLACEMENT = CleanUtils.cleanWordsReplacement("St-Laurent");

	private static final Pattern LB_PEARSON_ = CleanUtils.cleanWords("l\\. b\\. pearson", "lester b\\. pearson");
	private static final String LB_PEARSON_REPLACEMENT = CleanUtils.cleanWordsReplacement("LB Pearson");

	private static final Pattern HS_ = CleanUtils.cleanWords("h\\.s", "hs");
	private static final String HS_REPLACEMENT = CleanUtils.cleanWordsReplacement("HS");

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = STARTS_WITH_TO_VERS.matcher(tripHeadsign).replaceAll(Constants.EMPTY);
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = CAIRINE_WILSON_.matcher(tripHeadsign).replaceAll(CAIRINE_WILSON_REPLACEMENT);
		tripHeadsign = SARSFIELD_.matcher(tripHeadsign).replaceAll(SARSFIELD_REPLACEMENT);
		tripHeadsign = ST_LAURENT_.matcher(tripHeadsign).replaceAll(ST_LAURENT_REPLACEMENT);
		tripHeadsign = LB_PEARSON_.matcher(tripHeadsign).replaceAll(LB_PEARSON_REPLACEMENT);
		tripHeadsign = HS_.matcher(tripHeadsign).replaceAll(HS_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanLabel(tripHeadsign);
		return tripHeadsign; // DO NOT CLEAN, USED TO IDENTIFY TRIP IN REAL TIME API // <= TODO REALLY ???
	}

	private static final String N_ = "N ";

	@Override
	public boolean mergeHeadsign(@NotNull MTrip mTrip, @NotNull MTrip mTripToMerge) {
		if (!mTrip.getHeadsignValue().equals(mTripToMerge.getHeadsignValue())) {
			List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
			if (mTrip.getHeadsignValue().startsWith(N_) //
					&& mTrip.getHeadsignValue().equals(N_ + mTripToMerge.getHeadsignValue())) {
				mTrip.setHeadsignString(mTripToMerge.getHeadsignValue(), mTrip.getHeadsignId());
				return true;
			}
			if (mTripToMerge.getHeadsignValue().startsWith(N_) //
					&& mTripToMerge.getHeadsignValue().equals(N_ + mTrip.getHeadsignValue())) {
				mTripToMerge.setHeadsignString(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignId());
				return true;
			}
			if ("Special".equalsIgnoreCase(mTrip.getHeadsignValue()) //
					&& !"Special".equalsIgnoreCase(mTripToMerge.getHeadsignValue())) {
				mTrip.setHeadsignString(mTripToMerge.getHeadsignValue(), mTrip.getHeadsignId());
				return true;
			} else if ("Special".equalsIgnoreCase(mTripToMerge.getHeadsignValue()) //
					&& !"Special".equalsIgnoreCase(mTrip.getHeadsignValue())) {
				mTrip.setHeadsignString(mTrip.getHeadsignValue(), mTrip.getHeadsignId());
				return true;
			}
			if (mTrip.getRouteId() == 5L) {
				if (Arrays.asList( //
						"Waller", //
						"Rideau" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Rideau", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 9L) {
				if (Arrays.asList( //
						"Daly", //
						"Rideau" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Rideau", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 10L) {
				if (Arrays.asList( //
						"Lyon", //
						"Rideau" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Rideau", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Carleton", //
						"Hurdman" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Hurdman", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 11L) {
				if (Arrays.asList( //
						"Parliament", //
						"Parliament / Parlement" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Parliament / Parlement", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Parliament / Parlement", //
						"Rideau" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Rideau", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Lincoln Fields", //
						"Bayshore" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Bayshore", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 12L) {
				if (Arrays.asList( //
						"Rideau", //
						"Parliament ~ Parlement", //
						"Parliament / Parlement" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Parliament / Parlement", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 14L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Carlington" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Carlington", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 16L) {
				if (Arrays.asList( //
						"Westboro", //
						"Britannia" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Britannia", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Westboro" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Westboro", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 17L) {
				if (Arrays.asList( //
						"Rideau", //
						"Parliament / Parlement", //
						"Parliament" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Parliament", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Parliament / Parlement", //
						"Gatineau" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Gatineau", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 18L) {
				if (Arrays.asList( //
						"Parliament / Parlement", //
						"Parliament" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Parliament", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 19L) {
				if (Arrays.asList( //
						"Parliament ~ Parlement", //
						"Parliament / Parlement", //
						"Bank" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Bank", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 30L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 32L) {
				if (Arrays.asList( //
						"Blair", //
						"Place d'Orléans" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Place d'Orléans", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 33L) {
				if (Arrays.asList( //
						"Orléans", //
						"Portobello" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Portobello", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Blair", //
						"Place D'Orléans" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Place D'Orléans", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 34L) {
				if (Arrays.asList( //
						"Albert Bay", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Blair", //
						"Albert Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 38L) {
				if (Arrays.asList( //
						"Place D'Orléans", //
						"Jeanne D'Arc / Trim" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Jeanne D'Arc / Trim", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 40L) {
				if (Arrays.asList( //
						"Greenboro / Hurdman", //
						"Greenboro" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Greenboro", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 44L) {
				if (Arrays.asList( //
						"Hurdman", //
						"Gatineau" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Gatineau", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 56L) {
				if (Arrays.asList( //
						"Hurdman", //
						"King Edward" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("King Edward", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 58L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Lincoln Fields" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Lincoln Fields", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 61L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"St-Laurent" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("St-Laurent", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Terry Fox", //
						"Stittsville" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Stittsville", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 62L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"St-Laurent" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("St-Laurent", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 63L) {
				if (Arrays.asList( //
						"Mackenzie King Via Briarbrook", //
						"Tunney's Pasture Via Briarbrook" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Tunney's Pasture Via Briarbrook", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 64L) {
				if (Arrays.asList( //
						"Mackenzie King Via Morgan's Grant", //
						"Tunney's Pasture Via Morgan's Grant" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Tunney's Pasture Via Morgan's Grant", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 66L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Gatineau" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Gatineau", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Kanata-Solandt", //
						"Kanata" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Kanata", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 82L) {
				if (Arrays.asList( //
						"Lincoln Fields & Tunney's Pasture", //
						"Tunney's Pasture" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Tunney's Pasture", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 83L) {
				if (Arrays.asList( //
						"Baseline", //
						"Tunney's Pasture" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Tunney's Pasture", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 85L) {
				if (Arrays.asList( //
						"Gatineau", //
						"Lees", //
						"Lees / Gatineau" // ++
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Lees / Gatineau", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 86L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Elmvale" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Elmvale", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 87L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Greenboro" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Greenboro", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 93L) {
				if (Arrays.asList( //
						"Greenboro", //
						"Greenboro / Hurdman" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Greenboro / Hurdman", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 96L) {
				if (Arrays.asList( //
						"Hurdman / Greenboro", //
						"Greenboro" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Greenboro", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Merivale / 96b Hunt Club", //
						"Merivale" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Merivale", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 97L) {
				if (Arrays.asList( //
						"Hurdman", //
						"Bells Corners" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Bells Corners", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 98L) {
				if (Arrays.asList( //
						"Hurdman", //
						"Tunney's Pasture" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Tunney's Pasture", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 101L) {
				if (Arrays.asList( //
						"Moodie", //
						"Bayshore" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Bayshore", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 106L) {
				if (Arrays.asList( //
						"Riverside", //
						"Hurdman" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Hurdman", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 111L) {
				if (Arrays.asList( //
						"Carleton", //
						"Billings Bridge" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Billings Bridge", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 131L) {
				if (Arrays.asList( //
						"Fallingbrook", //
						"Convent Glen" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Convent Glen", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 138L) {
				if (Arrays.asList( //
						"St-Louis", //
						"Innes" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Innes", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 153L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Carlingwood" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Carlingwood", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Bayshore", //
						"Lincoln Fields" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Lincoln Fields", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 186L) {
				if (Arrays.asList( //
						"Merivale", //
						"Merivale / Slack" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Merivale / Slack", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 199L) {
				if (Arrays.asList( //
						"Leikin", //
						"Barrhaven" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Barrhaven", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Hurdman", //
						"Place D'Orléans" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Place D'Orléans", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 221L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 222L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 224L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 234L) {
				if (Arrays.asList( //
						"Tenth line", //
						"Tenth Line" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Tenth Line", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 228L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 231L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 232L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 233L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 234L) {
				if (Arrays.asList( //
						"Blair", //
						"Gatineau" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Gatineau", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 235L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 236L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 237L) {
				if (Arrays.asList( //
						"Blair", //
						"Albert / Bay" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Albert / Bay", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 252L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 256L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 257L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 261L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 262L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 263L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 264L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 265L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 267L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 268L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 270L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 271L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 272L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 273L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 275L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 277L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 278L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 282L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 283L) {
				if (Arrays.asList( //
						"Tunney's Pasture", //
						"Mackenzie King" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Mackenzie King", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 299L) {
				if (Arrays.asList( //
						"Hurdman", //
						"LeBreton" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("LeBreton", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 301L) {
				if (Arrays.asList( //
						"Bayshore Carlingwd", //
						"Bayshore Carlingwood" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Bayshore Carlingwood", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 303L) {
				if (Arrays.asList( //
						"Dunrobin Stittsville", //
						"Dunrobin Carp" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Dunrobin Carp", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 303L) {
				if (Arrays.asList( //
						"Dunrobin Stittsville", //
						"Dunrobin Carp" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Dunrobin Carp", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 305L) {
				if (Arrays.asList( //
						"North Gower / Manotick", //
						"North Gower" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("North Gower", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 401L) {
				if (Arrays.asList( //
						"Canadian Tire Centre", //
						"Canadian Tire Ctr" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Canadian Tire Ctr", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 402L) {
				if (Arrays.asList( //
						"Canadian Tire Centre", //
						"Canadian Tire Ctr" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Canadian Tire Ctr", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 403L) {
				if (Arrays.asList( //
						"Canadian Tire Centre", //
						"Canadian Tire Ctr" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Canadian Tire Ctr", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 404L) {
				if (Arrays.asList( //
						"Canadian Tire Centre", //
						"Canadian Tire Ctr" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Canadian Tire Ctr", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 405L) {
				if (Arrays.asList( //
						"Canadian Tire Centre", //
						"Canadian Tire Ctr" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Canadian Tire Ctr", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Scotiabank Place", //
						"Canadian Tire Centre" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Canadian Tire Centre", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 406L) {
				if (Arrays.asList( //
						"Canadian Tire Centre", //
						"Canadian Tire Ctr" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Canadian Tire Ctr", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"Scotiabank Place", //
						"Canadian Tire Centre" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Canadian Tire Centre", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 602L) {
				if (Arrays.asList( //
						"Mackenzie King", //
						"Rideau" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Rideau", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 609L) {
				if (Arrays.asList( //
						"Hurdman", //
						"Elmvale" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Elmvale", mTrip.getHeadsignId());
					return true;
				}
				if (Arrays.asList( //
						"De La Salle H.S", //
						"De La Salle" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("De La Salle", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 649L) {
				if (Arrays.asList( //
						"Hillcrest H.S", //
						"Hillcrest" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Hillcrest", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 661L) {
				if (Arrays.asList( //
						"Bell H.S", //
						"Bell" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Bell", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 665L) {
				if (Arrays.asList( //
						"Bell H.S", //
						"Bell" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Bell", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 669L) {
				if (Arrays.asList( //
						"Bell H.S", //
						"Bell" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Bell", mTrip.getHeadsignId());
					return true;
				}
			}
			if (mTrip.getRouteId() == 691L) {
				if (Arrays.asList( //
						"Deslauriers", //
						"Omer-Deslaurier H.S" //
				).containsAll(headsignsValues)) {
					mTrip.setHeadsignString("Omer-Deslaurier H.S", mTrip.getHeadsignId());
					return true;
				}
			}
			MTLog.logFatal("mergeHeadsign() > Can't merge headsign for trips %s and %s!", mTrip, mTripToMerge);
			return false; // DO NOT MERGE, USED TO IDENTIFY TRIP IN REAL TIME API
		}
		return super.mergeHeadsign(mTrip, mTripToMerge);
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = gStopName.toLowerCase(Locale.ENGLISH);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	private static final String CD = "CD";
	private static final String CF = "CF";
	private static final String DT = "DT";
	private static final String EE = "EE";
	private static final String EO = "EO";
	private static final String ER = "ER";
	private static final String NG = "NG";
	private static final String NO = "NO";
	private static final String WA = "WA";
	private static final String WD = "WD";
	private static final String WH = "WH";
	private static final String WI = "WI";
	private static final String WL = "WL";
	private static final String PLACE = "place";
	private static final String RZ = "RZ";
	private static final String SX = "SX";
	private static final String SNOW = "SNOW";
	private static final String SC = "SC";
	private static final String SD = "SD";
	private static final String SL = "SL";

	@SuppressWarnings("deprecation")
	@Override
	public int getStopId(@NotNull GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode.length() > 0 && Utils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		if ("SNO CAFÉ".equalsIgnoreCase(gStop.getStopId())) {
			return 9900001;
		} else if ("SNO-20B".equalsIgnoreCase(gStop.getStopId())) {
			return 9900002;
		} else if ("SNO -7B".equalsIgnoreCase(gStop.getStopId())) {
			return 9900003;
		} else if ("STOP - 8".equalsIgnoreCase(gStop.getStopId())) {
			return 9900004;
		} else if ("SNO-CAFÉ".equalsIgnoreCase(gStop.getStopId())) {
			return 9900005;
		}
		Matcher matcher = DIGITS.matcher(gStop.getStopId());
		if (matcher.find()) {
			int digits = Integer.parseInt(matcher.group());
			int stopId;
			if (gStop.getStopId().startsWith(EE)) {
				stopId = 100_000;
			} else if (gStop.getStopId().startsWith(EO)) {
				stopId = 200_000;
			} else if (gStop.getStopId().startsWith(NG)) {
				stopId = 300_000;
			} else if (gStop.getStopId().startsWith(NO)) {
				stopId = 400_000;
			} else if (gStop.getStopId().startsWith(WA)) {
				stopId = 500_000;
			} else if (gStop.getStopId().startsWith(WD)) {
				stopId = 600_000;
			} else if (gStop.getStopId().startsWith(WH)) {
				stopId = 700_000;
			} else if (gStop.getStopId().startsWith(WI)) {
				stopId = 800_000;
			} else if (gStop.getStopId().startsWith(WL)) {
				stopId = 900_000;
			} else if (gStop.getStopId().startsWith(PLACE)) {
				stopId = 1_000_000;
			} else if (gStop.getStopId().startsWith(RZ)) {
				stopId = 1_100_000;
			} else if (gStop.getStopId().startsWith(DT)) {
				stopId = 1_200_000;
			} else if (gStop.getStopId().startsWith(ER)) {
				stopId = 1_300_000;
			} else if (gStop.getStopId().startsWith(SNOW)) {
				stopId = 1_400_000;
			} else if (gStop.getStopId().startsWith(CD)) {
				stopId = 1_500_000;
			} else if (gStop.getStopId().startsWith(CF)) {
				stopId = 1_600_000;
			} else if (gStop.getStopId().startsWith(SX)) {
				stopId = 1_700_000;
			} else if (gStop.getStopId().startsWith(SC)) {
				stopId = 1_800_000;
			} else if (gStop.getStopId().startsWith(SD)) {
				stopId = 1_900_000;
			} else if (gStop.getStopId().startsWith("CB")) {
				stopId = 2_000_000;
			} else if (gStop.getStopId().startsWith("EN")) {
				stopId = 2_100_000;
			} else if (gStop.getStopId().startsWith("CE")) {
				stopId = 2_200_000;
			} else if (gStop.getStopId().startsWith("CA")) {
				stopId = 2_300_000;
			} else if (gStop.getStopId().startsWith("CK")) {
				stopId = 2_400_000;
			} else if (gStop.getStopId().startsWith(SL)) {
				stopId = 2_500_000;
			} else {
				MTLog.logFatal("Stop doesn't have an ID (start with) %s!", gStop);
				stopId = -1;
			}
			return stopId + digits;
		}
		MTLog.logFatal("Unexpected stop ID for %s!", gStop);
		return -1;
	}
}
