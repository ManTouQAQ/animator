package me.mantou.animator.render

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30C.*

class ButtonRender : Render{
    private var vao = 0
    private var vbo = 0
    private var ebo = 0

    private val vertices = floatArrayOf(
        -0.5f, -0.5f, 0f, 0f,
        0.5f, -0.5f, 1f, 0f,
        0.5f,  0.5f, 1f, 1f,
        -0.5f,  0.5f, 0f, 1f,
    )

    private val indices = intArrayOf(
        0, 1, 2,
        2, 3, 0,
    )

    override fun init() {
        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        val verticesBuffer = BufferUtils.createFloatBuffer(vertices.size)
            .put(vertices)
            .flip()
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW)

        ebo = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        val indicesBuffer = BufferUtils.createIntBuffer(indices.size)
            .put(indices)
            .flip()
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0)
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4)
        glEnableVertexAttribArray(1)

        glBindVertexArray(0)
    }

    override fun render() {
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    override fun destroy() {
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        glDeleteBuffers(ebo)
    }
}