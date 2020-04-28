package com.food.ordering.zinger.utils

import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_ACCEPTED
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_CANCELLED_BY_USER
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_COMPLETED
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_DELIVERED
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_PENDING
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_PLACED
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_READY
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_REFUND_COMPLETED
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_REFUND_INITIATED
import com.food.ordering.zinger.utils.AppConstants.ORDER_STATUS_TXN_FAILURE

object StatusHelper{

    fun getMessageDetail(status: String?): String{
        return when(status){
            "Pending" -> getStatusDetailedMessage(ORDER_STATUS_PENDING)
            "Transaction failed"  -> getStatusDetailedMessage(ORDER_STATUS_TXN_FAILURE)
            "Placed"  -> getStatusDetailedMessage(ORDER_STATUS_PLACED)
            "Cancelled by you"  -> getStatusDetailedMessage(ORDER_STATUS_CANCELLED_BY_USER)
            "Accepted"  -> getStatusDetailedMessage(ORDER_STATUS_ACCEPTED)
            "Cancelled by shop"  -> getStatusDetailedMessage(ORDER_STATUS_CANCELLED_BY_SELLER)
            "Ready"  -> getStatusDetailedMessage(ORDER_STATUS_READY)
            "Out for delivery"  -> getStatusDetailedMessage(ORDER_STATUS_OUT_FOR_DELIVERY)
            "Completed"  -> getStatusDetailedMessage(ORDER_STATUS_COMPLETED)
            "Delivered"  -> getStatusDetailedMessage(ORDER_STATUS_DELIVERED)
            "Refund initiated"  -> getStatusDetailedMessage(ORDER_STATUS_REFUND_INITIATED)
            "Refunded"  -> getStatusDetailedMessage(ORDER_STATUS_REFUND_COMPLETED)
            else -> status.toString()
        }
    }
    fun getStatusMessage(status: String?): String{
        return when(status){
            ORDER_STATUS_PENDING -> "Pending"
            ORDER_STATUS_TXN_FAILURE -> "Transaction failed"
            ORDER_STATUS_PLACED -> "Placed"
            ORDER_STATUS_CANCELLED_BY_USER -> "Cancelled by you"
            ORDER_STATUS_ACCEPTED -> "Accepted"
            ORDER_STATUS_CANCELLED_BY_SELLER -> "Cancelled by shop"
            ORDER_STATUS_READY -> "Ready"
            ORDER_STATUS_OUT_FOR_DELIVERY -> "Out for delivery"
            ORDER_STATUS_COMPLETED -> "Completed"
            ORDER_STATUS_DELIVERED -> "Delivered"
            ORDER_STATUS_REFUND_INITIATED -> "Refund initiated"
            ORDER_STATUS_REFUND_COMPLETED-> "Refunded"
            else -> status.toString()
        }
    }
    fun getStatusDetailedMessage(status: String): String{
        return when(status){
            ORDER_STATUS_PENDING -> "Transaction pending. Bank is still processing your transaction"
            ORDER_STATUS_TXN_FAILURE -> "Transaction failed. If money is deducted it will be refunded in 2 - 4 business hours"
            ORDER_STATUS_PLACED -> "Order has been successfully placed. Waiting for restaurant response"
            ORDER_STATUS_CANCELLED_BY_USER -> "Money will be refunded in 2 - 4 business hours"
            ORDER_STATUS_ACCEPTED -> "Order Accepted. Restaurant is preparing your food"
            ORDER_STATUS_CANCELLED_BY_SELLER -> "Money will be refunded in 2 - 4 business hours"
            ORDER_STATUS_READY -> "Your order is ready. Show your secret key and pick up your order from the restaurant"
            ORDER_STATUS_OUT_FOR_DELIVERY -> "Your order is out for delivery. Show your secret key when receiving your order"
            ORDER_STATUS_COMPLETED -> "Thanks for picking up your order from the restaurant"
            ORDER_STATUS_DELIVERED -> "Your order has been delivered. Thanks for ordering :)"
            ORDER_STATUS_REFUND_INITIATED -> "Refund initiated. Money will be refunded in 2 - 4 business hours"
            ORDER_STATUS_REFUND_COMPLETED-> "Your money has been refunded successfully"
            else -> status
        }
    }
}