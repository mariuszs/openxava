// ** I18N
// Calendar PL language
// Author: Artur Filipiak, <imagen@poczta.fm>
// January, 2004
// Encoding: UTF-8
Calendar._DN = new Array
("Niedziela", "Poniedzia³ek", "Wtorek", "Œroda", "Czwartek", "Pi¹tek", "Sobota", "Niedziela");

Calendar._SDN = new Array
("N", "Pn", "Wt", "Œr", "Cz", "Pt", "So", "N");

Calendar._MN = new Array
("Styczeñ", "Luty", "Marzec", "Kwiecieñ", "Maj", "Czerwiec", "Lipiec", "Sierpieñ", "Wrzesieñ", "PaŸdziernik", "Listopad", "Grudzieñ");

Calendar._SMN = new Array
("Sty", "Lut", "Mar", "Kwi", "Maj", "Cze", "Lip", "Sie", "Wrz", "PaŸ", "Lis", "Gru");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "O kalendarzu";

Calendar._TT["ABOUT"] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Autor: Mihai Bazon\n" + // don't translate this this ;-)
"Aby pobraæ najnowsz¹ wersjê, odwiedŸ: http://www.dynarch.com/projects/calendar/\n" +
"Dostêpny na licencji GNU LGPL. Zobacz szczegó³y na http://gnu.org/licenses/lgpl.html." +
"\n\n" +
"Wybór daty:\n" +
"- aby wybraæ rok u¿yj przycisków \xab, \xbb\n" +
"- aby wybraæ miesi¹c u¿yj przycisków " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + "\n" +
"- aby przyspieszyæ wybór przytrzymaj wciœniêty przycisk myszy nad ww. przyciskami.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"Wybór czasu:\n" +
"- aby zwiêkszyæ wartoœæ kliknij na dowolnym elemencie selekcji czasu\n" +
"- aby zmniejszyæ wartoœæ u¿yj dodatkowo klawisza Shift\n" +
"- mo¿esz równie¿ poruszaæ myszkê w lewo i prawo wraz z wciœniêtym lewym klawiszem.";

Calendar._TT["PREV_YEAR"] = "Poprz. rok (przytrzymaj dla menu)";
Calendar._TT["PREV_MONTH"] = "Poprz. miesi¹c (przytrzymaj dla menu)";
Calendar._TT["GO_TODAY"] = "Poka¿ dziœ";
Calendar._TT["NEXT_MONTH"] = "Nast. miesi¹c (przytrzymaj dla menu)";
Calendar._TT["NEXT_YEAR"] = "Nast. rok (przytrzymaj dla menu)";
Calendar._TT["SEL_DATE"] = "Wybierz datê";
Calendar._TT["DRAG_TO_MOVE"] = "Przesuñ okienko";
Calendar._TT["PART_TODAY"] = " (dziœ)";

//the following is to inform that "%s" is to be the first day of week
//%s will be replaced with the day name.
Calendar._TT["DAY_FIRST"] = "Poka¿ %s jako pierwszy";

//This may be locale-dependent.  It specifies the week-end days, as an array
//of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
//means Monday, etc.
Calendar._TT["WEEKEND"] = "0,6";

Calendar._TT["CLOSE"] = "Zamknij";
Calendar._TT["TODAY"] = "Dziœ";
Calendar._TT["TIME_PART"] = "(Shift-)klik | drag, aby zmieniæ wartoœæ";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%Y-%m-%d";
Calendar._TT["TT_DATE_FORMAT"] = "%a, %b %e";

Calendar._TT["WK"] = "wk";
Calendar._TT["TIME"] = "Czas:";