<template>
    <div>
        <div class="F-2">
            <!-- 个人动态显示 -->
            <div class="F-2-content">
                <div class="F-2-A"><!--头部展示-->
                    <div class="A-1">
                        我的动态
                    </div>
                    <div @click="TurnToDynamicEdit" class="A-2">
                        +发布动态
                    </div>
                    <div class="A-3">
                        <span>允许他人查看</span>
                        <div class="switch">
                            <el-form ref="form" :model="form">
                                <el-switch v-model="allowview"></el-switch>
                            </el-form>
                        </div>
                    </div>
                </div>
                <div v-for="(item, index) in DynamicList" :key="item.id" class="F-2-B"><!--列表展示-->
                    <div class="textcontent">
                        <div class="text">
                            <!-- 用户动态展示列表文字内容部分 -->
                            {{ item.content }}
                        </div>
                    </div>
                    <!-- 底部点赞，评论，转发，删除 -->
                    <div class="clickfunction">
                        <ul>
                            <li>
                                <div class="like-wrapper">
                                    <svg class="icon like-icon" :class="{ active: item.isLike }" @click="Like(item.id)"
                                        :disabled="loadingStates[item.id]">
                                        <use xlink:href="#icon-dianzan_kuai"></use>
                                    </svg>
                                    <span class="like-count">{{ item.likesNum || 0 }}</span>
                                </div>
                            </li>
                            <li v-for="(icon, i) in iconList.slice(1, 3)" :key="i + 1">
                                <svg class="icon" @click="handleFeatureClick(i)">
                                    <use :xlink:href="icon"></use>
                                </svg>
                            </li>
                            <!-- 修改删除按钮部分 -->
                            <li>
                                <img src='../../../../assets/icons/delete.svg' class="delete-icon"
                                    @click="handleDelete(item.id)" alt="删除">
                            </li>
                        </ul>
                    </div>
                </div>
                <div v-if="nodynamictip" class="nodynamic">您当前还没有发布过动态哦！</div>
            </div>
        </div>

        <infinite-loading spinner="spiral" @infinite="infiniteHandler" :distance="200" class="infinite-loading-wrap">
            <div slot="spinner">加载中...</div>
            <div slot="no-more">暂无更多数据</div>
            <div slot="no-results">No results Data</div>
            <div slot="error" slot-scope="{ trigger }">
                Error Data, click
                <a href="javascript:;" @click="trigger">here</a> to retry
            </div>
        </infinite-loading>
    </div>
</template>

<script>
import qs from 'qs'

import InfiniteLoading from 'vue-infinite-loading'

export default {
    name: 'Dynamic',
    props: {
        userId: {
            require: true,
            default: 0,
            type: Number,
        },
    },
    data() {
        return {
            // 是否允许他人查看
            DynamicList: [],
            allowview: false,
            loadingStates: {},
            iconList: ['#icon-dianzan_kuai', '#icon-pinglun', '#icon-zhuanfa'],
            // 点赞的激活状态
            likeactive: false,
            userId: this.userId,
            page: 1,
            hasMore: true,
            headers: {
                token: localStorage.getItem('token')
            },
            nodynamictip: false
        }
    },
    created() { },
    components: {
        InfiniteLoading,
    },
    methods: {
        async infiniteHandler($state) {
            if (!this.hasMore) {
                $state.complete()
                return
            }

            try {
                const res = await this.$axios.get('/blink/list/self', {
                    params: {
                        userId: this.userId,
                        page: this.page
                    },
                    headers: { token: localStorage.getItem('token') }
                })

                if (res.data?.status) {
                    const records = res.data.data.records
                    this.DynamicList = this.DynamicList.concat(
                        records.map(item => ({
                            ...item,
                            isLike: item.isLike ?? false,
                            likesNum: item.likesNum || 0
                        }))
                    )

                    this.page++
                    this.hasMore = this.page <= res.data.data.pages
                    $state.loaded()

                    if (!this.hasMore) {
                        $state.complete()
                    }
                }
            } catch (error) {
                $state.error()
                console.error("加载失败:", error)
            }
        },
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
        },
        //跳转至编辑动态页面
        TurnToDynamicEdit() {
            var routeUrl = this.$router.resolve({ path: '/DynamicEdit' })
            window.open(routeUrl.href, '_blank');
        },
        // 添加功能提示
        handleFeatureClick(index) {
            const features = ['评论', '分享'];
            this.$message({
                message: `当前暂未开发${features[index]}功能哦，敬请期待更多功能~`,
                type: 'info',
                duration: 3000,
                customClass: 'feature-message'
            });
        },
        // 新增删除方法
        async handleDelete(blinkId) {
            try {
                await this.$confirm('确定删除这条动态吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })

                await this.$axios.delete('/blink', {
                    params: { blinkId },
                    headers: { token: localStorage.getItem('token') }
                })

                this.DynamicList = this.DynamicList.filter(item => item.id !== blinkId)
                this.$message.success('删除成功')
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error(error.response?.data?.message || '删除失败')
                }
            }
        }
    }
}
</script>

<style scoped lang="less">
/* 动态模块部分 */
.F-2 {
    width: 100%;
    border-radius: 5px;
    display: flex;
    background: rgb(255, 255, 255);
}

.F-2 .F-2-content {
    width: 100%;
    padding: 0 15px 15px 15px;

    .nodynamic {
        width: 100%;
        font-size: 2rem;
        font-weight: 600;
        color: black;
        text-align: center;
        margin: 30px auto;
    }
}

/* 动态头部展示模块 */
.F-2 .F-2-content .F-2-A {
    width: 100%;
    height: 50px;
    display: flex;
}

.F-2 .F-2-content .F-2-A .A-1 {
    width: 150px;
    line-height: 50px;
    font-size: 20px;
    font-weight: 600;
    text-align: center;
    color: rgb(0, 0, 0);
}

.F-2 .F-2-content .F-2-A .A-2 {
    width: 100px;
    line-height: 50px;
    font-size: 16px;
    font-weight: 500;
    text-align: center;
    color: rgb(0, 160, 246);
    cursor: pointer;
}

.F-2 .F-2-content .F-2-A .A-3 {
    width: 400px;
    line-height: 50px;
    font-size: 16px;
    font-weight: 500;
    text-align: center;
    display: flex;
    margin-left: 100px;
    color: rgb(0, 0, 0);
}

.F-2 .F-2-content .F-2-A .A-3 div {
    display: flex;
    align-items: center;
    margin-left: 5px;
}

/* 动态列表部分展示模块 */
.F-2 .F-2-content .F-2-B {
    width: 100%;
    min-height: 200px;
    border: 1.5px solid rgb(183, 183, 183);
    border-radius: 10px;
}

/* 头像条 */
.F-2 .F-2-content .F-2-B .headurl {
    width: 100%;
    height: 70px;
    padding: 10px;
    display: flex;
}

.F-2 .F-2-content .F-2-B .headurl .img {
    width: 50px;
    height: 50px;
    overflow: hidden;
    /* border: 3px solid rgb(234, 234, 234); */
    border-radius: 50%;
    margin: auto 0;
}

.F-2 .F-2-content .F-2-B .headurl .img img {
    width: 100%;
}

.F-2 .F-2-content .F-2-B .headurl .username {
    width: 150px;
    margin: auto 20px;
    line-height: 50px;
    font-size: 20px;
    font-weight: 600;
    color: rgb(79, 215, 239);
}

.F-2 .F-2-content .F-2-B .textcontent {
    width: 900px;
    height: 200px;
    border-radius: 5px;
    background: rgb(245, 243, 243);
    margin: 0 auto 20px;
    color: black;
    font-size: 16px;
}

.F-2 .F-2-content .F-2-B .clickfunction {
    width: 100%;
    height: 50px;
}

.F-2 .F-2-content .F-2-B .clickfunction ul {
    display: flex;
}

.F-2 .F-2-content .F-2-B .clickfunction li {
    width: 25%;
    height: 50px;
    text-align: center;
}

.F-2 .F-2-content .F-2-B .clickfunction li svg {
    fill: rgb(118, 118, 118);
    transition: all .3s;
    cursor: pointer;
    width: 30px;
    height: 30px;
}

.F-2 .F-2-content .F-2-B .clickfunction li svg:hover {
    -webkit-transform: scale(1.3) translateY(-5px);
    fill: rgb(79, 224, 234);
}

.F-2 .F-2-content .F-2-B .clickfunction li:nth-child(1) svg:hover {
    -webkit-transform: rotate(360deg) scale(1.3);
    fill: rgb(1, 221, 67);
}

.F-2 .F-2-content .F-2-B .clickfunction li:nth-child(1) svg:active {
    transform: rotate(-360deg) scale(0.8);
    animation: rotation 3s linear infinite;
    fill: rgb(5, 245, 253);
}

/* 伪类表现激活后的样式 */
.F-2 .F-2-content .F-2-B .clickfunction li:nth-child(1) svg.active {
    fill: rgb(5, 245, 253);
}

.delete-icon {
    width: 24px;
    height: 24px;
    cursor: pointer;
    transition: all 0.3s;
    fill: #999; // 如果SVG支持currentColor

    &:hover {
        transform: scale(1.2);
        opacity: 0.8;
    }

    &:active {
        transform: scale(0.9);
    }
}

/* 修改其他图标容器对齐方式 */
.clickfunction ul {
    display: flex;
    align-items: center;
    justify-content: space-around;
    height: 100%;
}
</style>