package skac.svgraph

import com.github.skac112.vgutils.Point

/**
 * Utility class for creating svg d attr string values for paths.
 */
case class PathString(val str: String = "") {
  private def addStr(str: String) = this.str match {
    case "" => PathString(str)
    case _ => PathString(s"${this.str} $str")
  }

  private def segLetter(baseLetter: Char, relative: Boolean): String = if (relative)
    baseLetter.toLower.toString else baseLetter.toUpper.toString

  private def flagStr(flag: Boolean): String = flag match {
    case true => "1"
    case _ => "0"
  }

  def arc(relative: Boolean, rx: Double, ry: Double, xAxisRot: Double,
    largeArcFlag: Boolean, sweepFlag: Boolean, x: Double, y: Double): PathString = {
    val new_str = s"${this.segLetter('a', relative)} $rx $ry $xAxisRot ${this.flagStr(largeArcFlag)} ${this.flagStr(sweepFlag)} $x $y"
    addStr(new_str)
  }

  def circleArc(relative: Boolean, r: Double, largeArcFlag: Boolean,
   sweepFlag: Boolean, x: Double, y: Double): PathString =
   this.arc(relative, r, r, 0, largeArcFlag, sweepFlag, x, y)

  // def arcRel(rx: Double, ry: Double, xAxisRot: Double, largeArcFlag: Boolean,
  //  sweepFlag: Boolean, x: Double, y: Double): PathString =
	// 	this._arc(true, rx, ry, xAxisRot, largeArcFlag, sweepFlag, x, y)

	// def arc(rx: Double, ry: Double, xAxisRot: Double, largeArcFlag: Boolean,
  //  sweepFlag: Boolean, x: Double, y: Double): PathString =
	// 	this._arc(false, rx, ry, xAxisRot, largeArcFlag, sweepFlag, x, y)

  def moveTo(relative: Boolean, x: Double, y: Double): PathString = {
    val new_str = s"${this.segLetter('m', relative)} $x $y"
    addStr(new_str)
	}

  def moveTo(relative: Boolean, p: Point): PathString =
   moveTo(relative, p.x, p.y)

  // def moveToRel(x: Double, y: Double): PathString = this._moveTo(true, x, y)

	// def moveTo(x: Double, y: Double): PathString = this._moveTo(false, x, y)

	def close: PathString = addStr("Z")

  def map(mapF: PathString => PathString): PathString = mapF(this)
  def flatMap(fMapF: PathString => PathString): PathString = map(fMapF)
  def withFilter(filtF: PathString => Boolean): PathString = this
}
