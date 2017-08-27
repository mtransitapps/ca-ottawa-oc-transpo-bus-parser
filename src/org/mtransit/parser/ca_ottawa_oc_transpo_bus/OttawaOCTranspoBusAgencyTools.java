package org.mtransit.parser.ca_ottawa_oc_transpo_bus;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
// http://www.octranspo.com/files/google_transit.zip
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
		Matcher matcher = DIGITS.matcher(gRoute.getRouteId());
		if (matcher.find()) {
			return Integer.parseInt(matcher.group());
		}
		System.out.printf("\nUnexpected route ID for '%s'!\n", gRoute);
		System.exit(-1);
		return -1l;
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
	private static final String BLOHM = "Blohm";
	private static final String BLOSSOM_PARK = "Blossom Pk";
	private static final String BRIDLEWOOD = "Bridlewood";
	private static final String BRITANNIA = "Britannia";
	private static final String BROOKFIELD_HIGH_SCHOOL = "Brookfield " + HIGH_SCHOOL;
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
	private static final String É_S = "ÉS";
	private static final String É_S_DE_LA_SALLE = É_S + " De La Salle";
	private static final String É_S_DESLAURIERS = É_S + " Deslauriers";
	private static final String É_S_GISELE_LALONDE = É_S + " Gisèle Lalonde";
	private static final String É_S_LOUIS_RIEL = É_S + " Louis-Riel";
	private static final String EAGLESON = "Eagleson";
	private static final String ELMVALE = "Elmvale";
	private static final String ESPRIT = "Esprit";
	private static final String EXPERIMENTAL_FARM = "Experimental Farm";
	private static final String FALLOWFIELD = "Fallowfield";
	private static final String GATINEAU = "Gatineau";
	private static final String GLOUCESTER_NORTH = "Gloucester North";
	private static final String GOLFLINKS = "Golflinks";
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
	private static final String HUNTMAR = "Huntmar";
	private static final String HURDMAN = "Hurdman";
	private static final String HURDMAN_STA = HURDMAN + " Sta";
	private static final String IMMACULATA_HIGH_SCHOOL = "Immaculata " + HIGH_SCHOOL;
	private static final String INNOVATION = "Innovation";
	private static final String JEANNE_D_ARC = "Jeanne d'Arc";
	private static final String JEANNE_D_ARC_STA = JEANNE_D_ARC + " Sta";
	private static final String KANATA = "Kanata";
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
	private static final String ORLEANS = "Orléans";
	private static final String OSGOODE = "Osgoode";
	private static final String OTTAWA = "Ottawa";
	private static final String OTTAWA_ROCKCLIFFE = OTTAWA + "-Rockcliffe";
	private static final String PAGE = "Page";
	private static final String PETRIE_ISL = "Petrie Isl";
	private static final String PINECREST = "Pinecrest";
	private static final String PINEVIEW = "Pineview";
	private static final String PLACE_D_ORLEANS = "Pl d'" + ORLEANS;
	private static final String PORTOBELLO = "Portobello";
	private static final String QUEENSWAY_TER = "Queensway Ter";
	private static final String QUEENSWOOD_HTS = "Queenswood Hts";
	private static final String RENAUD = "Renaud";
	private static final String RICHMOND = "Richmond";
	private static final String RIDEAU_CTR = "Rideau Ctr";
	private static final String RIDGEMONT = "Ridgemont";
	private static final String RIDGEMONT_HIGH_SCHOOL = RIDGEMONT + " " + HIGH_SCHOOL;
	private static final String RIVERVIEW = "Riverview";
	private static final String SARSFIELD = "Sarsfield";
	private static final String SOUTH_KEYS = "South Keys";
	private static final String SOUTHKEYS = "Southkeys";
	private static final String ST_JOSEPH = "St Joseph";
	private static final String ST_LAURENT = "St Laurent";
	private static final String ST_LAURENT_STA = ST_LAURENT + " Sta";
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
	private static final String ROUTE_11 = DOWNTOWN + RLN_SEP + BAYSHORE;
	private static final String ROUTE_12 = BLAIR + RLN_SEP + RIDEAU_CTR;
	private static final String ROUTE_14 = ST_LAURENT + RLN_SEP + CARLINGTON;
	private static final String ROUTE_16 = MAIN + RLN_SEP + BRITANNIA;
	private static final String ROUTE_18 = ST_LAURENT + RLN_SEP + RIDEAU_CTR;
	private static final String ROUTE_19 = ST_LAURENT + RLN_SEP + BANK;
	private static final String ROUTE_20 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_21 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_22 = ALBERT + SLASH + BAY + RLN_SEP + MILLENNIUM;
	private static final String ROUTE_23 = BLAIR + RLN_SEP + "Rothwell Heights";
	private static final String ROUTE_24 = ALBERT + SLASH + BAY + RLN_SEP + BEACON_HILL;
	private static final String ROUTE_26 = BLAIR + RLN_SEP + PINEVIEW;
	private static final String ROUTE_27 = GATINEAU + RLN_SEP + ORLEANS;
	private static final String ROUTE_28 = BLACKBURN + " " + HAMLET + RLN_SEP + BLAIR;
	private static final String ROUTE_30 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_31 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_32 = ST_LAURENT + RLN_SEP + GREENBORO; // not official
	private static final String ROUTE_33 = PLACE_D_ORLEANS + COLON + ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_34 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_35 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_37 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_38 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_40 = GATINEAU + RLN_SEP + BLOSSOM_PARK;
	private static final String ROUTE_41 = HURDMAN + RLN_SEP + WALKLEY;
	private static final String ROUTE_42 = BLAIR + RLN_SEP + HURDMAN;
	private static final String ROUTE_43 = HURDMAN + RLN_SEP + CONROY;
	private static final String ROUTE_44 = GATINEAU + RLN_SEP + BILLINGS_BRIDGE; // not official
	private static final String ROUTE_47 = HAWTHORNE + RLN_SEP + ST_LAURENT;
	private static final String ROUTE_48 = ELMVALE + RLN_SEP + BILLINGS_BRIDGE + SLASH + HURDMAN;
	private static final String ROUTE_49 = ELMVALE + RLN_SEP + HURDMAN;
	private static final String ROUTE_50 = TUNNEY_S_PASTURE + RLN_SEP + LINCOLN_FIELDS;
	private static final String ROUTE_56 = HURDMAN + RLN_SEP + TUNNEY_S_PASTURE;
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
	private static final String ROUTE_140 = MC_CARTHY + RLN_SEP + HURDMAN;
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
	private static final String ROUTE_228 = ALBERT + SLASH + BAY + RLN_SEP + NAVAN;
	private static final String ROUTE_231 = ALBERT + SLASH + BAY + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_232 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_233 = ALBERT + SLASH + BAY + RLN_SEP + ORLEANS;
	private static final String ROUTE_234 = GATINEAU + RLN_SEP + ORLEANS;
	private static final String ROUTE_235 = ALBERT + SLASH + BAY + RLN_SEP + ESPRIT;
	private static final String ROUTE_237 = ALBERT + SLASH + BANK + RLN_SEP + JEANNE_D_ARC;
	private static final String ROUTE_252 = MACKENZIE_KING + RLN_SEP + BELLS_CORNERS;
	private static final String ROUTE_256 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_261 = MACKENZIE_KING + RLN_SEP + STITTSVILLE;
	private static final String ROUTE_262 = MACKENZIE_KING + RLN_SEP + STITTSVILLE;
	private static final String ROUTE_263 = MACKENZIE_KING + RLN_SEP + STITTSVILLE;
	private static final String ROUTE_264 = MACKENZIE_KING + RLN_SEP + TERRY_FOX;
	private static final String ROUTE_265 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_267 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_268 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_269 = MACKENZIE_KING + RLN_SEP + KANATA;
	private static final String ROUTE_270 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_271 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_272 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_273 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_277 = MACKENZIE_KING + RLN_SEP + BARRHAVEN;
	private static final String ROUTE_282 = MACKENZIE_KING + RLN_SEP + PINECREST;
	private static final String ROUTE_283 = MACKENZIE_KING + RLN_SEP + RICHMOND;
	private static final String ROUTE_290 = MC_CARTHY + RLN_SEP + HURDMAN;
	private static final String ROUTE_291 = HURDMAN + RLN_SEP + HERONGATE;
	private static final String ROUTE_293 = GATINEAU + RLN_SEP + BLOSSOM_PARK;
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
	private static final String ROUTE_505 = StringUtils.EMPTY; // TODO ?
	private static final String ROUTE_506 = StringUtils.EMPTY; // TODO ?
	private static final String ROUTE_508 = StringUtils.EMPTY; // TODO ?
	private static final String ROUTE_520 = HAWKESBURY + RLN_SEP + OTTAWA + RLN_SEP + GATINEAU;
	private static final String ROUTE_540 = StringUtils.EMPTY; // TODO ?
	private static final String ROUTE_545 = StringUtils.EMPTY; // TODO ?
	private static final String ROUTE_555 = "Casselman" + RLN_SEP + OTTAWA + RLN_SEP + GATINEAU;
	private static final String ROUTE_602 = É_S_DE_LA_SALLE + RLN_SEP + HURDMAN;
	private static final String ROUTE_611 = É_S_GISELE_LALONDE + RLN_SEP + CHAPEL_HL;
	private static final String ROUTE_612 = É_S_GISELE_LALONDE + RLN_SEP + CHAPEL_HL;
	private static final String ROUTE_613 = IMMACULATA_HIGH_SCHOOL + RLN_SEP + HURDMAN_STA;
	private static final String ROUTE_618 = É_S_LOUIS_RIEL + RLN_SEP + MILLENNIUM_STA;
	private static final String ROUTE_619 = É_S_LOUIS_RIEL + RLN_SEP + BLAIR_STA;
	private static final String ROUTE_622 = COLONEL_BY_HIGH_SCHOOL + RLN_SEP + BLACKBURN + SLASH + PAGE;
	private static final String ROUTE_630 = COLONEL_BY_HIGH_SCHOOL + RLN_SEP + MILLENNIUM;
	private static final String ROUTE_632 = É_S_GISELE_LALONDE + RLN_SEP + QUEENSWOOD_HTS;
	private static final String ROUTE_633 = LESTER_B_PEARSON_HIGH_SCHOOL + RLN_SEP + ST_LAURENT_STA;
	private static final String ROUTE_640 = BROOKFIELD_HIGH_SCHOOL + RLN_SEP + GREENBORO_STA;
	private static final String ROUTE_641 = É_S_LOUIS_RIEL + RLN_SEP + MEADOWGLEN + SLASH + ORLEANS;
	private static final String ROUTE_644 = CANTERBURY_HIGH_SCHOOL + RLN_SEP + GREENBORO;
	private static final String ROUTE_648 = É_S_LOUIS_RIEL + RLN_SEP + YOUVILLE + SLASH + ST_JOSEPH;
	private static final String ROUTE_649 = HILLCREST_HIGH_SCHOOL + RLN_SEP + GREENBORO;
	private static final String ROUTE_660 = BELL_HIGH_SCHOOL + RLN_SEP + INNOVATION;
	private static final String ROUTE_661 = BELL_HIGH_SCHOOL + RLN_SEP + TERRY_FOX_STA;
	private static final String ROUTE_665 = BELL_HIGH_SCHOOL + RLN_SEP + BRIDLEWOOD;
	private static final String ROUTE_669 = BELL_HIGH_SCHOOL + RLN_SEP + BAYSHORE + SLASH + CARLING;
	private static final String ROUTE_670 = ST_PIUS_X_HIGH_SCHOOL + RLN_SEP + VAAN + SLASH + WOODROFFE;
	private static final String ROUTE_674 = ALL_STS_CATHOLIC_AND_STEPHEN_LEACOCK_SCHOOLS + RLN_SEP + INNOVATION + SLASH + HINES;
	private static final String ROUTE_678 = É_S_LOUIS_RIEL + RLN_SEP + JEANNE_D_ARC_STA;
	private static final String ROUTE_681 = BELL_HIGH_SCHOOL + RLN_SEP + BRIDLEWOOD;
	private static final String ROUTE_691 = É_S_DESLAURIERS + RLN_SEP + BAYSHORE_STA;
	private static final String ROUTE_698 = RIDGEMONT_HIGH_SCHOOL + SLASH + ST_PATRICK_S_HIGH_SCHOOL + RLN_SEP + GREENBORO;
	private static final String ROUTE_970 = StringUtils.EMPTY; // TODO
	private static final String ROUTE_975 = StringUtils.EMPTY; // TODO

	@Override
	public String getRouteLongName(GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteLongName())) {
			Matcher matcher = DIGITS.matcher(gRoute.getRouteId());
			if (matcher.find()) {
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
				case 11: return ROUTE_11;
				case 12: return ROUTE_12;
				case 14: return ROUTE_14;
				case 16: return ROUTE_16;
				case 18: return ROUTE_18;
				case 19: return ROUTE_19;
				case 20: return ROUTE_20;
				case 21: return ROUTE_21;
				case 22: return ROUTE_22;
				case 23: return ROUTE_23;
				case 24: return ROUTE_24;
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
				case 40: return ROUTE_40;
				case 41: return ROUTE_41;
				case 42: return ROUTE_42;
				case 43: return ROUTE_43;
				case 44: return ROUTE_44;
				case 47: return ROUTE_47;
				case 48: return ROUTE_48;
				case 49: return ROUTE_49;
				case 50: return ROUTE_50;
				case 56: return ROUTE_56;
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
				case 228: return ROUTE_228;
				case 231: return ROUTE_231;
				case 232: return ROUTE_232;
				case 233: return ROUTE_233;
				case 234: return ROUTE_234;
				case 235: return ROUTE_235;
				case 237: return ROUTE_237;
				case 252: return ROUTE_252;
				case 256: return ROUTE_256;
				case 261: return ROUTE_261;
				case 262: return ROUTE_262;
				case 263: return ROUTE_263;
				case 264: return ROUTE_264;
				case 265: return ROUTE_265;
				case 267: return ROUTE_267;
				case 268: return ROUTE_268;
				case 269: return ROUTE_269;
				case 270: return ROUTE_270;
				case 271: return ROUTE_271;
				case 272: return ROUTE_272;
				case 273: return ROUTE_273;
				case 277: return ROUTE_277;
				case 282: return ROUTE_282;
				case 283: return ROUTE_283;
				case 290: return ROUTE_290;
				case 291: return ROUTE_291;
				case 293: return ROUTE_293;
				case 298: return ROUTE_298;
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
				case 508: return ROUTE_508;
				case 520: return ROUTE_520;
				case 540: return ROUTE_540;
				case 545: return ROUTE_545;
				case 555: return ROUTE_555;
				case 602: return ROUTE_602;
				case 611: return ROUTE_611;
				case 612: return ROUTE_612;
				case 613: return ROUTE_613;
				case 618: return ROUTE_618;
				case 619: return ROUTE_619;
				case 622: return ROUTE_622;
				case 630: return ROUTE_630;
				case 632: return ROUTE_632;
				case 633: return ROUTE_633;
				case 640: return ROUTE_640;
				case 641: return ROUTE_641;
				case 644: return ROUTE_644;
				case 648: return ROUTE_648;
				case 649: return ROUTE_649;
				case 660: return ROUTE_660;
				case 661: return ROUTE_661;
				case 665: return ROUTE_665;
				case 669: return ROUTE_669;
				case 670: return ROUTE_670;
				case 674: return ROUTE_674;
				case 678: return ROUTE_678;
				case 681: return ROUTE_681;
				case 691: return ROUTE_691;
				case 698: return ROUTE_698;
				case 970: return ROUTE_970;
				case 975: return ROUTE_975;
				// @formatter:on
				}
			}
			if (isGoodEnoughAccepted()) {
				return "Route " + gRoute.getRouteShortName();
			}
			System.out.printf("\nUnexpected route long name for '%s'!\n", gRoute);
			System.exit(-1);
			return null;
		}
		return super.getRouteLongName(gRoute);
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

	private static final String ROUTE_COLOR_RAPID_BLUE_DARK = "293D9B";
	private static final String ROUTE_COLOR_RAPID_BLUE_LIGHT = "3871C2";

	private static final String ROUTE_COLOR_FREQUENT_ORANGE_DARK = "F14623";
	private static final String ROUTE_COLOR_FREQUENT_ORANGE_LIGHT = "F68712";

	private static final String ROUTE_COLOR_LOCAL_GRAY_DARK = "4F4C4C";
	private static final String ROUTE_COLOR_LOCAL_GRAY_LIGHT = "7B7979";

	private static final String ROUTE_COLOR_CONNEXION_PURPLE_DARK = "8D188F";
	private static final String ROUTE_COLOR_CONNEXION_PURPLE_LIGHT = "5D2491";

	private static final String ROUTE_COLOR_SHOPPER = null;
	private static final String ROUTE_COLOR_EVENT = null;
	private static final String ROUTE_COLOR_RURAL_PARTNERS = null;
	private static final String ROUTE_COLOR_SCHOOL = "FFD800"; // School bus yellow

	private static final Collection<Integer> BLACK_ROUTES = Arrays.asList(new Integer[] { //
			1, 2, 4, 5, 7, 8, 9, 12, 14, 16, 18, 19, 33, //
					63, 85, 86, 87, 91, 92, 93, 94, 95, 96, 97, 98, 99, //
					101, 103, 104, 106, 107, 111, 112, 114, 116, 118, 120, 121, 122, 123, 124, //
					126, 127, 128, 129, 130, 131, 132, 134, 135, 137, 143, 144, 146, 147, 148, 149, //
					150, 151, 152, 153, 154, 156, 159, 161, 162, 164, 165, 166, 167, 168, 170, 171, 172, 173, 174, //
					175, 176, 177, 178, 185, 196, 198, //
					222, 224, 233, 234, 235, 237, //
					252, 256, 264, 265, 267, 268, 269, //
					270, 271, 272, 273, 277, 282, 290, 293, 298, //
					301, 302, 303, 304, 305, //
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
					221, 228, 231, 232, //
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
			500, 502, 503, 505, 506, 509, 515, 520, 523, 524, 525, 526, 530, 535, 538, 541, 542, 543, //
					551, 552, 553, 555, 556, 557, 558, 559, 565 //
			});

	private static final Collection<Integer> SCHOOL_ROUTES = Arrays.asList(new Integer[] { //
			602, 611, 612, 613, 618, 619, 622, 630, 632, 633, 640, 641, 644, 648, 649, //
					660, 661, 665, 669, 670, 674, 678, 681, 691, 698 //
			});

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			Matcher matcher = DIGITS.matcher(gRoute.getRouteId());
			if (matcher.find()) {
				int routeId = Integer.parseInt(matcher.group());
				switch (routeId) {
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
				if (100 <= routeId && routeId <= 199) {
					return ROUTE_COLOR_LOCAL_GRAY_DARK;
				}
				if (200 <= routeId && routeId <= 299) {
					return ROUTE_COLOR_CONNEXION_PURPLE_DARK;
				}
				if (300 <= routeId && routeId <= 399) {
					return ROUTE_COLOR_SHOPPER;
				}
				if (400 <= routeId && routeId <= 499) {
					return ROUTE_COLOR_EVENT;
				}
				if (500 <= routeId && routeId <= 599) {
					return ROUTE_COLOR_RURAL_PARTNERS;
				}
				if (600 <= routeId && routeId <= 699) {
					return ROUTE_COLOR_SCHOOL;
				}
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
			}
			if (isGoodEnoughAccepted()) {
				return null;
			}
			System.out.printf("\nNo route color for '%s'!", gRoute);
			System.exit(-1);
			return null;
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (mRoute.getId() == 179L) {
			if (gTrip.getDirectionId() == 0 && "0".equals(gTrip.getTripHeadsign())) {
				mTrip.setHeadsignString(CITIGATE, gTrip.getDirectionId());
				return;
			} else if (gTrip.getDirectionId() == 1 && "1".equals(gTrip.getTripHeadsign())) {
				mTrip.setHeadsignString(FALLOWFIELD, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.getId() == 660L) {
			if (gTrip.getDirectionId() == 0 && "0".equals(gTrip.getTripHeadsign())) {
				mTrip.setHeadsignString(BELL_HIGH_SCHOOL, gTrip.getDirectionId());
				return;
			} else if (gTrip.getDirectionId() == 1 && "1".equals(gTrip.getTripHeadsign())) {
				mTrip.setHeadsignString(INNOVATION, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.getId() == 698L) {
			if (gTrip.getDirectionId() == 0 && "0".equals(gTrip.getTripHeadsign())) {
				mTrip.setHeadsignString(ST_PATRICK_S_HIGH_SCHOOL, gTrip.getDirectionId());
				return;
			} else if (gTrip.getDirectionId() == 1 && "1".equals(gTrip.getTripHeadsign())) {
				mTrip.setHeadsignString(BLOHM, gTrip.getDirectionId());
				return;
			}
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
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
			return false; // DO NOT MERGE, USED TO IDENTIFY TRIP IN REAL TIME API
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

	@Override
	public int getStopId(GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode != null && stopCode.length() > 0 && Utils.isDigitsOnly(stopCode)) {
			return Integer.valueOf(stopCode); // using stop code as stop ID
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
			int stopId = 0;
			if (gStop.getStopId().startsWith(EE)) {
				stopId = 100000;
			} else if (gStop.getStopId().startsWith(EO)) {
				stopId = 200000;
			} else if (gStop.getStopId().startsWith(NG)) {
				stopId = 300000;
			} else if (gStop.getStopId().startsWith(NO)) {
				stopId = 400000;
			} else if (gStop.getStopId().startsWith(WA)) {
				stopId = 500000;
			} else if (gStop.getStopId().startsWith(WD)) {
				stopId = 600000;
			} else if (gStop.getStopId().startsWith(WH)) {
				stopId = 700000;
			} else if (gStop.getStopId().startsWith(WI)) {
				stopId = 800000;
			} else if (gStop.getStopId().startsWith(WL)) {
				stopId = 900000;
			} else if (gStop.getStopId().startsWith(PLACE)) {
				stopId = 1000000;
			} else if (gStop.getStopId().startsWith(RZ)) {
				stopId = 1100000;
			} else if (gStop.getStopId().startsWith(DT)) {
				stopId = 1200000;
			} else if (gStop.getStopId().startsWith(ER)) {
				stopId = 1300000;
			} else if (gStop.getStopId().startsWith(SNOW)) {
				stopId = 1400000;
			} else if (gStop.getStopId().startsWith(CD)) {
				stopId = 1500000;
			} else if (gStop.getStopId().startsWith(CF)) {
				stopId = 1600000;
			} else if (gStop.getStopId().startsWith(SX)) {
				stopId = 1700000;
			} else if (gStop.getStopId().startsWith(SC)) {
				stopId = 1800000;
			} else if (gStop.getStopId().startsWith(SD)) {
				stopId = 1900000;
			} else {
				System.out.printf("\nStop doesn't have an ID (start with) %s!\n", gStop);
				System.exit(-1);
				stopId = -1;
			}
			return stopId + digits;
		}
		System.out.printf("\nUnexpected stop ID for %s!\n", gStop);
		System.exit(-1);
		return -1;
	}
}
