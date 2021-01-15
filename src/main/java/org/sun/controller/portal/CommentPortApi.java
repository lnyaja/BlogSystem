package org.sun.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sun.pojo.Comment;
import org.sun.response.ResponseResult;
import org.sun.services.ICommentService;

@RestController
@RequestMapping("/portal/comment")
public class CommentPortApi {

    @Autowired
    private ICommentService commentService;

    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment){
        return commentService.postComment(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId){
        return commentService.deleteCommentById(commentId);
    }

    @GetMapping("/list/{articleId}/{page}/{size}")
    public ResponseResult listComments(@PathVariable("articleId") String articleId,
                                       @PathVariable("page") int page,
                                       @PathVariable("size") int size){
        return commentService.listCommentByArticleId(articleId, page, size);
    }
}
