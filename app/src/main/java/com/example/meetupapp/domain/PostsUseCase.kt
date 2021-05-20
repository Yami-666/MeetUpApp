//package com.example.meetup.domain
//
//import com.example.meetup.pojo.post.PostUi
//import com.example.meetup.repository.IPostsRepository
//import com.example.meetupapp.util.DataState
//import com.example.meetupapp.util.FlowStateList
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.mapNotNull
//import javax.inject.Inject
//
//class PostsUseCase @Inject constructor(
//    private val postsRepository: IPostsRepository
//) : IPostsUseCase {
//    override suspend fun invoke(): FlowStateList<PostUi> {
//        val postsRepo = postsRepository.getAllPosts()
//        val postsRepoData = postsRepo.mapNotNull {
//            when(it) {
//                is DataState.Success -> {
//                    it.data.map { postDomain ->
//                        postDomain.mapToUi()
//                    }
//                }
//                else -> {
//                    emptyList()
//                }
//            }
//        }
//        return postsRepoData.map {
//            DataState.Success(it)
//        }
//    }
//}
//
//interface IPostsUseCase : IUseCase.Out<FlowStateList<PostUi>>
//
