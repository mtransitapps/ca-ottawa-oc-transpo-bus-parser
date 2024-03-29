package org.mtransit.parser.ca_ottawa_oc_transpo_bus;

import static org.mtransit.commons.RegexUtils.DIGITS;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.provider.OttawaOCTranspoProviderCommons;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

// https://www.octranspo.com/en/plan-your-trip/travel-tools/developers/
// https://www.octranspo.com/fr/planifiez/outils-dinformation/developpeurs/
public class OttawaOCTranspoBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new OttawaOCTranspoBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN_FR;
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "OC Transpo";
	}

	@Override
	public boolean excludeRoute(@NotNull GRoute gRoute) {
		if (gRoute.getRouteShortName().equals("1")) {
			return EXCLUDE; // misclassified as bus(3) instead of rail(2)
		}
		return super.excludeRoute(gRoute);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Nullable
	@Override
	public Long convertRouteIdPreviousChars(@NotNull String previousChars) {
		switch (previousChars) {
		case "SNO":
			return 506L;
		}
		return null;
	}

	@Nullable
	public String getRouteIdCleanupRegex() {
		return "\\-\\d+$";
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR = "C80D1A";

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.removeVia(tripHeadsign);
		return OttawaOCTranspoProviderCommons.cleanTripHeadsign(tripHeadsign);
	}

	@NotNull
	private String[] getIgnoredWords() {
		return new String[]{
				"TOH"
		};
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, gStopName, getIgnoredWords());
		gStopName = CleanUtils.fixMcXCase(gStopName);
		gStopName = CleanUtils.cleanBounds(gStopName);
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

	@Override
	public int getStopId(@NotNull GStop gStop) {
		final String stopCode = getStopCode(gStop);
		if (stopCode.length() > 0 && CharUtils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		//noinspection deprecation
		final String stopId1 = gStop.getStopId();
		if ("SNO CAFÉ".equalsIgnoreCase(stopId1)) {
			return 9_900_001;
		} else if ("SNO-20B".equalsIgnoreCase(stopId1)) {
			return 9_900_002;
		} else if ("SNO -7B".equalsIgnoreCase(stopId1)) {
			return 9_900_003;
		} else if ("STOP - 8".equalsIgnoreCase(stopId1)) {
			return 9_900_004;
		} else if ("SNO-CAFÉ".equalsIgnoreCase(stopId1)) {
			return 9_900_005;
		}
		final Matcher matcher = DIGITS.matcher(stopId1);
		if (matcher.find()) {
			final int digits = Integer.parseInt(matcher.group());
			final int stopId;
			if (stopId1.startsWith(EE)) {
				stopId = 100_000;
			} else if (stopId1.startsWith(EO)) {
				stopId = 200_000;
			} else if (stopId1.startsWith(NG)) {
				stopId = 300_000;
			} else if (stopId1.startsWith(NO)) {
				stopId = 400_000;
			} else if (stopId1.startsWith(WA)) {
				stopId = 500_000;
			} else if (stopId1.startsWith(WD)) {
				stopId = 600_000;
			} else if (stopId1.startsWith(WH)) {
				stopId = 700_000;
			} else if (stopId1.startsWith(WI)) {
				stopId = 800_000;
			} else if (stopId1.startsWith(WL)) {
				stopId = 900_000;
			} else if (stopId1.startsWith(PLACE)) {
				stopId = 1_000_000;
			} else if (stopId1.startsWith(RZ)) {
				stopId = 1_100_000;
			} else if (stopId1.startsWith(DT)) {
				stopId = 1_200_000;
			} else if (stopId1.startsWith(ER)) {
				stopId = 1_300_000;
			} else if (stopId1.startsWith(SNOW)) {
				stopId = 1_400_000;
			} else if (stopId1.startsWith(CD)) {
				stopId = 1_500_000;
			} else if (stopId1.startsWith(CF)) {
				stopId = 1_600_000;
			} else if (stopId1.startsWith(SX)) {
				stopId = 1_700_000;
			} else if (stopId1.startsWith(SC)) {
				stopId = 1_800_000;
			} else if (stopId1.startsWith(SD)) {
				stopId = 1_900_000;
			} else if (stopId1.startsWith("CB")) {
				stopId = 2_000_000;
			} else if (stopId1.startsWith("EN")) {
				stopId = 2_100_000;
			} else if (stopId1.startsWith("CE")) {
				stopId = 2_200_000;
			} else if (stopId1.startsWith("CA")) {
				stopId = 2_300_000;
			} else if (stopId1.startsWith("CK")) {
				stopId = 2_400_000;
			} else if (stopId1.startsWith(SL)) {
				stopId = 2_500_000;
			} else if (stopId1.startsWith("WJ")) {
				stopId = 2_600_000;
			} else if (stopId1.startsWith("NI")) {
				stopId = 2_700_000;
			} else if (stopId1.startsWith("NH")) {
				stopId = 2_800_000;
			} else if (stopId1.startsWith("NC")) {
				stopId = 2_900_000;
			} else if (stopId1.startsWith("AJ")) {
				stopId = 3_000_000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (start with) %s!", gStop.toStringPlus());
			}
			return stopId + digits;
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop.toStringPlus());
	}
}
