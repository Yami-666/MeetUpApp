//package com.example.meetup.repository
//
//import com.example.meetup.pojo.post.PostDomain
//import com.example.meetup.source.locale.IPostsLocaleDataSource
//import com.example.meetupapp.source.remote.IPostsRemoteDataSource
//import com.example.meetupapp.util.DataState
//import com.example.meetupapp.util.FlowStateList
//import kotlinx.coroutines.flow.flow
//import javax.inject.Inject
//
//class PostsRepository @Inject constructor(
//    private val postsRemoteDataSource: IPostsRemoteDataSource,
//    private val postsLocaleDataSource: IPostsLocaleDataSource,
//) : IPostsRepository {
//    override suspend fun getAllPosts() : FlowStateList<PostDomain> = flow {
//        emit(DataState.Loading)
//        kotlinx.coroutines.delay(1000)
//        try {
//            val postsNetwork = postsRemoteDataSource.getPosts()
//            postsNetwork.map { postModel ->
//                postModel.mapFromModel(postModel)
//            }.forEach { postDomain ->
//                postsLocaleDataSource.insertPost(postDomain.mapToEntity(postDomain))
//            }
//
//            val cachedPosts = postsLocaleDataSource.getAllPartsCache()
//            val postsDomain = cachedPosts.map { postCacheEntity ->
//                postCacheEntity.mapFromEntity(postCacheEntity)
//            }
//            emit(DataState.Success(postsDomain))
//        } catch (e: Exception) {
//            emit(DataState.Error(e))
//        }
//    }
//
//}
//
//interface IPostsRepository {
//    suspend fun getAllPosts() : FlowStateList<PostDomain>
//}