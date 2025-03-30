<template>
  <div class="contain">
    <div class="F-3">
      <!-- 表白墙的部分 -->
      <div class="F-3-content">
        <!-- 预留动画部分 -->
        <div class="publishlove">
          <a @click="Publish">我要发布</a>
        </div>
        <!-- 当没有数据时显示 -->
        <div class="nodata" v-if="DynamicList.length == 0">
          <p>当前没有动态哦</p>
        </div>
        <!-- 表白墙正文显示区域 -->
        <div class="heartsay" v-for="item in DynamicList" :key="item.id">
          <div class="heartA">
            <div class="img">
              <img :src="item.user.avatarUrl" alt="">
            </div>
            <div class="username">
              {{ item.user.username }}
            </div>
          </div>
          <div class="heartB">
            <!-- 表白墙动态内容部分 -->
            <div>
              {{ item.content }}
            </div>
          </div>
          <div class="heartC">
            <ul>
              <li>
                <div class="like-wrapper">
                  <svg class="icon like-icon" :class="{ active: item.isLike || false }" @click="Like(item.id)"
                    :disabled="loadingStates[item.id]" aria-hidden="true">
                    <use xlink:href="#icon-dianzan_kuai"></use>
                  </svg>
                  <span class="like-count">
                    {{ item.likesNum || 0 }}
                  </span>
                </div>
              </li>
              <li v-for="(icon, i) in iconList" :key="i">
                <svg class="icon" aria-hidden="true">
                  <use :xlink:href="icon"></use>
                </svg>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import qs from 'qs'

export default {
  data() {
    return {
      avatarUrl: '',
      username: '',
      iconList: ['#icon-pinglun', '#icon-zhuanfa', '#icon-gengduo'],
      loadingStates: {},  // 加载状态跟踪
      config: {
        params: {
          page: 0,
          schoolCode: null,
        }
      },
      // 动态列表
      DynamicList: []
    }
  },
  created() {
    this.GetData()
  },
  methods: {
    GetData() {
      // 添加headers携带token
      const config = {
        params: this.config.params,
        headers: {
          token: localStorage.getItem("token")
        }
      }

      this.$axios.get('/blink/list', config).then((res) => {
        if (res.data?.status) {
          this.DynamicList = res.data.data.records.map(item => ({
            ...item,
            isLike: item.isLike ?? false  // 前端默认值处理
          }))
        }
      }).catch(console.error)
    },
    // 跳转至编辑动态页面
    Publish() {
      var routeUrl = this.$router.resolve({ path: './DynamicEdit' })
      window.open(routeUrl.href, '_blank');
    },
    // 点赞函数
    async Like(blinkId) {
      if (!blinkId || this.loadingStates[blinkId]) return;

      const index = this.DynamicList.findIndex(item => item.id === blinkId);
      if (index === -1) return;

      const target = this.DynamicList[index];
      if (!target) return;

      // 初始化字段
      if (typeof target.isLike === 'undefined') {
        this.$set(target, 'isLike', false);
      }
      if (typeof target.likesNum === 'undefined') {
        this.$set(target, 'likesNum', 0);
      }

      const originalLike = target.isLike;
      const originalCount = target.likesNum;

      try {
        this.$set(this.loadingStates, blinkId, true);

        // 乐观更新
        target.isLike = !originalLike;
        target.likesNum += originalLike ? -1 : 1;

        const res = await this.$axios.post('/blink/like',
          qs.stringify({ blinkId }),
          { headers: { token: localStorage.getItem("token") } }
        );

        if (res.data.code !== 200) {
          throw new Error(res.data.message || '操作失败');
        }

        // 同步后端准确数据
        target.likesNum = res.data.data.num;
        target.isLike = res.data.data.status;

      } catch (error) {
        // 回滚
        target.isLike = originalLike;
        target.likesNum = originalCount;
        this.$message.error(error.message);
      } finally {
        this.$delete(this.loadingStates, blinkId);
      }
    }
  },

}
</script>

<style scoped>
.contain {
  /* 保留空容器样式 */
}

.F-3 {
  width: 100%;
  min-height: 200px;
  border-radius: 5px;
  background: linear-gradient(to right, pink, rgb(251, 205, 213), pink);
}

.F-3-content {
  width: 100%;
}

/* 合并重复的.F-3选择器 */
.F-3 .publishlove {
  width: 100%;
  line-height: 100px;
  text-align: right;
  font-size: 20px;
  font-weight: 600;
  padding-right: 15%;
  color: rgb(64, 189, 251);
}

.F-3 .publishlove a {
  cursor: pointer;
}

.heartsay {
  width: 100%;
  min-height: 200px;
  border-bottom: 1px solid white;
}

.nodata {
  width: 100%;
  line-height: 100%;
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  color: aliceblue;
}

/* 合并嵌套层级 */
.heartA {
  width: 100%;
  height: 100px;
  padding-left: 20px;
  display: flex;
  align-items: center;
}

.heartA .img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(205, 205, 205, 0.5);
  text-align: center;
}

.heartA .img img {
  width: 100%;
  height: 100%;
}

.heartA .username {
  line-height: 100%;
  font-size: 20px;
  font-weight: 600;
  margin-left: 20px;
}

.heartB {
  width: 95%;
  min-height: 200px;
  margin: 10px auto;
  border-radius: 5px;
  background: rgba(241, 234, 234, 0.6);
  border-color: white;
  transition: all 0.3s;
}

.heartB:hover {
  border: 4px solid rgba(255, 255, 255);
}

/* 合并ul相关样式 */
.heartC ul {
  width: 100%;
  height: 60px;
  display: flex;
}

.heartC li {
  width: 25%;
  height: 100%;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.heartC svg {
  width: 50%;
  height: 50%;
  margin-top: 10px;
  fill: rgb(255, 255, 255);
  transition: all 0.2s linear;
  cursor: pointer;
}

/* 合并交互状态 */
.heartC svg:hover {
  transform: scale(1.3);
  fill: rgb(51, 239, 236);
}

.heartC svg:active {
  transform: scale(0.8);
  fill: rgb(238, 90, 90);
}

/* 合并点赞相关样式 */
.like-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 16px;
  transition: background-color 0.2s;
}

.like-wrapper:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.like-icon {
  width: 24px !important;
  height: 24px !important;
  margin-top: 0 !important;
}

.like-count {
  margin-left: 4px;
  font-size: 14px;
  color: #fff;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 合并激活状态 */
.icon.active {
  color: #ff5652;
  fill: currentColor;
  filter: drop-shadow(0 2px 4px rgba(255, 86, 82, 0.3));
}

/* 优化覆盖规则 */
.heartC li:nth-child(1) svg.active {
  fill: rgb(51, 239, 236);
}

.heartC li:hover svg {
  transform: none;
  fill: currentColor;
}

.like-wrapper:hover .like-icon {
  transform: scale(1.1);
  transition: transform 0.2s cubic-bezier(0.18, 0.89, 0.32, 1.28);
}
</style>
