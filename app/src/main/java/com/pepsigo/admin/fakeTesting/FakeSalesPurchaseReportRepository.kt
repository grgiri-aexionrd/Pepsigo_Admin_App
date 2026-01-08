package com.pepsigo.admin.fakeTesting

import com.pepsigo.admin.model.SalesPurchaseReportUi
import com.pepsigo.admin.repository.SalesPurchaseReportRepository

class FakeSalesPurchaseReportRepository: SalesPurchaseReportRepository {
    override suspend fun fetchSalesRegister(
        startDate: String,
        endDate: String,
        customerId: Int?
    ): Result<List<SalesPurchaseReportUi>> {
        val fakeData = listOf(
            SalesPurchaseReportUi(
                id = 1,
                invoiceNumber = "INV-001",
                userId = 1,
                madeByUserId = 201,
                saleDate = "2024-01-15",
                subTotal = 1000.0,
                discountBeforeTax = 50.0,
                tax = 95.0,
                discountAfterTax = 20.0,
                totalAmount = 1025.0,
                invoiceStatus = "sale",
                name = "Hitesh Kumar",
                businessName = "Manjunatha Agencies"
            ),
            SalesPurchaseReportUi(
                id = 2,
                invoiceNumber = "INV-002",
                userId = 1,
                madeByUserId = 201,
                saleDate = "2024-01-16",
                subTotal = 500.0,
                discountBeforeTax = 25.0,
                tax = 47.5,
                discountAfterTax = 10.0,
                totalAmount = 512.5,
                invoiceStatus = "return",
                name = "Hitesh Kumar",
                businessName = "Manjunatha Agencies"
            ),
            SalesPurchaseReportUi(
                id = 3,
                invoiceNumber = "INV-003",
                userId = 1,
                madeByUserId = 203,
                saleDate = "2024-01-17",
                subTotal = 750.0,
                discountBeforeTax = 30.0,
                tax = 68.0,
                discountAfterTax = 15.0,
                totalAmount = 773.0,
                invoiceStatus = "cancelled",
                name = "Hitesh Kumar",
                businessName = "Manjunatha Agencies"
            ),
            SalesPurchaseReportUi(
                id = 4,
                invoiceNumber = "INV-004",
                userId = 2,
                madeByUserId = 202,
                saleDate = "2024-01-20",
                subTotal = 2000.0,
                discountBeforeTax = 100.0,
                tax = 190.0,
                discountAfterTax = 40.0,
                totalAmount = 2050.0,
                invoiceStatus = "sale",
                name = "Anita Singh",
                businessName = "Singh Traders"
            ),
            SalesPurchaseReportUi(
                id = 5,
                invoiceNumber = "INV-005",
                userId = 2,
                madeByUserId = 202,
                saleDate = "2024-01-21",
                subTotal = 1200.0,
                discountBeforeTax = 60.0,
                tax = 114.0,
                discountAfterTax = 25.0,
                totalAmount = 1229.0,
                invoiceStatus = "return",
                name = "Anita Singh",
                businessName = "Singh Traders"
            )

        )

val resultData = try {
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                    val startDt = sdf.parse(startDate)
                    val endDt = sdf.parse(endDate)
                    fakeData.filter { entry ->
                        val saleDt = try { sdf.parse(entry.saleDate) } catch (e: Exception) { null }
                        val inRange = if (saleDt != null) {
                            !saleDt.before(startDt) && !saleDt.after(endDt)
                        } else {
                            false
                        }
                        val matchesCustomer = customerId?.let { entry.userId == it } ?: true
                        inRange && matchesCustomer
                    }
                } catch (e: Exception) {
    customerId?.let { id -> fakeData.filter { it.userId == id } } ?: fakeData
                }

        return Result.success(resultData)
    }

    override suspend fun fetchPurchaseRegister(
        startDate: String,
        endDate: String,
        vendorId: Int?
    ): Result<List<SalesPurchaseReportUi>> {
        val fakeData = listOf(
            SalesPurchaseReportUi(
                id = 6,
                invoiceNumber = "INV-006",
                userId = 3,
                madeByUserId = 301,
                saleDate = "2024-02-01",
                subTotal = 1500.0,
                discountBeforeTax = 75.0,
                tax = 142.5,
                discountAfterTax = 30.0,
                totalAmount = 1537.5,
                invoiceStatus = "sale",
                name = "Ravi Narayan",
                businessName = "Fresh Mart Wholesale"
            ),
            SalesPurchaseReportUi(
                id = 7,
                invoiceNumber = "INV-007",
                userId = 3,
                madeByUserId = 301,
                saleDate = "2024-02-03",
                subTotal = 800.0,
                discountBeforeTax = 40.0,
                tax = 76.0,
                discountAfterTax = 15.0,
                totalAmount = 821.0,
                invoiceStatus = "return",
                name = "Ravi Narayan",
                businessName = "Fresh Mart Wholesale"
            ),
            SalesPurchaseReportUi(
                id = 8,
                invoiceNumber = "INV-008",
                userId = 3,
                madeByUserId = 302,
                saleDate = "2024-02-05",
                subTotal = 1200.0,
                discountBeforeTax = 60.0,
                tax = 114.0,
                discountAfterTax = 20.0,
                totalAmount = 1234.0,
                invoiceStatus = "cancelled",
                name = "Ravi Narayan",
                businessName = "Fresh Mart Wholesale"
            ),
            SalesPurchaseReportUi(
                id = 9,
                invoiceNumber = "INV-009",
                userId = 4,
                madeByUserId = 302,
                saleDate = "2024-02-02",
                subTotal = 2000.0,
                discountBeforeTax = 100.0,
                tax = 190.0,
                discountAfterTax = 50.0,
                totalAmount = 2040.0,
                invoiceStatus = "sale",
                name = "Meena Sharma",
                businessName = "Sharma Distributors"
            ),
            SalesPurchaseReportUi(
                id = 10,
                invoiceNumber = "INV-010",
                userId = 4,
                madeByUserId = 303,
                saleDate = "2024-02-06",
                subTotal = 650.0,
                discountBeforeTax = 32.5,
                tax = 61.75,
                discountAfterTax = 10.0,
                totalAmount = 669.25,
                invoiceStatus = "return",
                name = "Meena Sharma",
                businessName = "Sharma Distributors"
            ),
            SalesPurchaseReportUi(
                id = 11,
                invoiceNumber = "INV-011",
                userId = 4,
                madeByUserId = 304,
                saleDate = "2024-02-08",
                subTotal = 1400.0,
                discountBeforeTax = 70.0,
                tax = 133.0,
                discountAfterTax = 25.0,
                totalAmount = 1438.0,
                invoiceStatus = "cancelled",
                name = "Meena Sharma",
                businessName = "Sharma Distributors"
            )
        )

        val resultData = try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val startDt = sdf.parse(startDate)
            val endDt = sdf.parse(endDate)
            fakeData.filter { entry ->
                val saleDt = try {
                    sdf.parse(entry.saleDate)
                } catch (e: Exception) {
                    null
                }
                val inRange = if (saleDt != null) {
                    !saleDt.before(startDt) && !saleDt.after(endDt)
                } else {
                    false
                }
                val matchesCustomer = vendorId?.let { entry.userId == it } ?: true
                inRange && matchesCustomer
            }
        } catch (e: Exception) {
            vendorId?.let { id -> fakeData.filter { it.userId == id } } ?: fakeData
        }

        return Result.success(resultData)
    }


}
