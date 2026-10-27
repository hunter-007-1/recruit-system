import {
  get,
  put,
  _delete,
  post
} from '@/lin/plugin/axios'
class Position {
  static async getByState(state, page, count) {
    const res = await get(`recruit/position/page/${state}`, {
      page: page,
      count: count
    })
    return res
  }

  static async getById(id) {
    const res = await get(`recruit/position/${id}`)
    return res
  }

  static async updateState(id, state) {
    const res = await put(`recruit/position/state/${id}?state=${state}`)
    return res
  }

  static async updateHits(id) {
    const res = await put(`recruit/position/hits/${id}`)
    return res
  }

  static async getByHrId(hrID, page, count) {
    const res = await get(`recruit/position/page/hr/${hrID}`, {
      page: page,
      count: count
    })
    return res
  }

  static async delete(id) {
    const res = await _delete(`recruit/position/${id}`)
    return res
  }

  static async updatePosition(id, data) {
    const res = await put(`recruit/position/${id}`, data)
    return res
  }

  static async create(data) {
    const res = await post('recruit/position', data)
    return res
  }

  static async getByKeyword(page, count, keyword) {
    const res = await get('recruit/position/page/keyword', {
      page: page,
      count: count,
      keyword: keyword
    })
    return res
  }

  static async sortByHits() {
    const res = await get('recruit/position/sort')
    return res
  }

  // ========== 区块链相关接口 ==========

  /**
   * 校验职位数据的链上一致性
   * @param {number} id 职位ID
   * @returns {Object} 校验结果 {consistent, localHash, chainHash, txId, message}
   */
  static async verifyChainConsistency(id) {
    const res = await get(`recruit/position/chain/verify/${id}`)
    return res
  }

  /**
   * 手动触发职位数据重新上链
   * @param {number} id 职位ID
   * @returns {Object} 更新结果
   */
  static async retryChainUpload(id) {
    const res = await put(`recruit/position/chain/retry/${id}`)
    return res
  }
}

export {
  Position
}
