/**
 * 请求的方法
 */

import axios from 'axios'
import base from './base'
const api ={
    /**
     * 商品列表
     */
    getTicketsList(params){
        return axios.get(base.TicketsList,{
            params
        })
    }
    
}

export default api