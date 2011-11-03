package se.lu.chemphys.sms.util

import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import au.com.bytecode.opencsv.CSVReader

trait TableRow extends IndexedSeq[String] {
  
	def apply(colName: String): String
	def apply(index: Int): String
	
}

trait DataTable {

	val columnNames: Seq[String]
	val rows: Seq[TableRow]
	
}

class TsvDataTable(file: File) extends DataTable {

	private val ioReader = new BufferedReader(new FileReader(file))
	private val reader = new CSVReader(ioReader, '\t')
	
	val columnNames = reader.readNext().toSeq
	
	private val colIndexLookup = columnNames.zipWithIndex.toMap
	private val nOfCols = columnNames.length
	
	private def getRows: Stream[TableRow] = {
		val row = reader.readNext()
		if(row == null) {
			reader.close()
			Stream.empty 
		}
		else Stream.cons(new TsvTableRow(row), getRows)
	}
	
	lazy val rows = getRows
	
	private class TsvTableRow(row: Array[String]) extends TableRow{
		assert(row.length == nOfCols, "DataTable must have the same number of columns in every row!")
		override def length = row.length
		def apply(i: Int) = row(i)
		def apply(colName: String) = row(colIndexLookup(colName))
	}
}