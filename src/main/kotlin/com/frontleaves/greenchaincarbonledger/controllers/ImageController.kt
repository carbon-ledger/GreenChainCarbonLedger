package com.frontleaves.greenchaincarbonledger.controllers

import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.ClassPathResource
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.FileInputStream
import java.util.regex.Pattern

/**
 * ImageController
 *
 * 图片控制器, 用于返回图片信息
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@RestController
@RequestMapping("/image")
class ImageController {

    /**
     * getImageAvatar
     *
     * 获取用户账户的头像图片
     *
     * @param uuid String
     * @return ByteArray?
     */
    @RequestMapping("/avatar/{uuid}")
    fun getImageAvatar(@PathVariable uuid: String, response: HttpServletResponse) {
        // 获取图片
        val getImage = try {
            log.debug("\t> jpg判断")
            FileInputStream("upload/avatar/${uuid}.jpg")
        } catch (e: Exception) {
            try {
                log.debug("\t> jpeg判断")
                FileInputStream("upload/avatar/${uuid}.jpeg")
            } catch (e: Exception) {
                try {
                    log.debug("\t> png判断")
                    FileInputStream("upload/avatar/${uuid}.png")
                } catch (e: Exception) {
                    log.debug("\t> NotFounded")
                    ClassPathResource("images/no-image-p.png").file.inputStream()
                }
            }
        }
        log.info("============================================================")
        this.writeImageToResponse(getImage, response)
    }

    /**
     * getImageOrganizeLicense
     *
     * 获取组织账户的营业执照图片
     *
     * @param uuid String
     * @return ByteArray?
     */
    @RequestMapping("/license/{uuid}")
    fun getImageOrganizeLicense(@PathVariable uuid: String, response: HttpServletResponse) {
        log.info("[Controller] 执行 getImageOrganizeLicense 方法")
        // 获取图片
        val getImage = if (Pattern.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", uuid)) {
            try {
                log.debug("\t> jpg判断")
                FileInputStream("upload/license/${uuid}_organize.jpg")
            } catch (e: Exception) {
                try {
                    log.debug("\t> jpeg判断")
                    FileInputStream("upload/license/${uuid}_organize.jpeg")
                } catch (e: Exception) {
                    try {
                        log.debug("\t> png判断")
                        FileInputStream("upload/license/${uuid}_organize.png")
                    } catch (e: Exception) {
                        log.debug("\t> NotFounded")
                        ClassPathResource("images/no-image.png").file.inputStream()
                    }
                }
            }
        } else {
            log.debug("\t> 自定义图片")
            try {
                FileInputStream("upload/license/${uuid}")
            } catch (e: Exception) {
                ClassPathResource("images/no-image.png").file.inputStream()
            }
        }
        log.info("============================================================")
        this.writeImageToResponse(getImage, response)
    }

    /**
     * getImageAdminAuthorize
     *
     * 获取监管账户的授权图片
     *
     * @param uuid String
     * @return ByteArray?
     */
    @RequestMapping("/authorize/{uuid}")
    fun getImageAdminAuthorize(@PathVariable uuid: String, response: HttpServletResponse) {
        // 获取图片
        val getImage = if (Pattern.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", uuid)) {
            try {
                log.debug("\t> jpg判断")
                FileInputStream("upload/license/${uuid}_authorize.jpg")
            } catch (e: Exception) {
                try {
                    log.debug("\t> jpeg判断")
                    FileInputStream("upload/license/${uuid}_authorize.jpeg")
                } catch (e: Exception) {
                    try {
                        log.debug("\t> png判断")
                        FileInputStream("upload/license/${uuid}_authorize.png")
                    } catch (e: Exception) {
                        log.debug("\t> NotFounded")
                        ClassPathResource("images/no-image.png").file.inputStream()
                    }
                }
            }
        } else {
            log.debug("\t> 自定义图片")
            try {
                FileInputStream("upload/license/${uuid}")
            } catch (e: Exception) {
                ClassPathResource("images/no-image.png").file.inputStream()
            }
        }
        log.info("============================================================")
        this.writeImageToResponse(getImage, response)
    }

    /**
     * imageLegalIdCard
     *
     * 获取法人账户的身份证图片
     *
     * @param uuid String
     * @param type String
     * @return ByteArray?
     */
    @RequestMapping("/legal/{type}/{uuid}")
    fun imageLegalIdCard(@PathVariable uuid: String, @PathVariable type: String, response: HttpServletResponse) {
        if ("front".equals(type, ignoreCase = true)) {
            // 获取图片
            val getImage =
                if (Pattern.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", uuid)) {
                    try {
                        log.debug("\t> jpg判断")
                        FileInputStream("upload/legal_id_card/${uuid}_front.jpg")
                    } catch (e: Exception) {
                        try {
                            log.debug("\t> jpeg判断")
                            FileInputStream("upload/legal_id_card/${uuid}_front.jpeg")
                        } catch (e: Exception) {
                            try {
                                log.debug("\t> png判断")
                                FileInputStream("upload/legal_id_card/${uuid}_front.png")
                            } catch (e: Exception) {
                                log.debug("\t> NotFounded")
                                ClassPathResource("images/no-image.png").file.inputStream()
                            }
                        }
                    }
                } else {
                    log.debug("\t> 自定义图片")
                    try {
                        FileInputStream("upload/legal_id_card/${uuid}")
                    } catch (e: Exception) {
                        ClassPathResource("images/no-image.png").file.inputStream()
                    }
                }
            log.info("============================================================")
            this.writeImageToResponse(getImage, response)
        } else {
            // 获取图片
            val getImage =
                if (Pattern.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", uuid)) {
                    try {
                        log.debug("\t> jpg判断")
                        FileInputStream("upload/legal_id_card/${uuid}_back.jpg")
                    } catch (e: Exception) {
                        try {
                            log.debug("\t> jpeg判断")
                            FileInputStream("upload/legal_id_card/${uuid}_back.jpeg")
                        } catch (e: Exception) {
                            try {
                                log.debug("\t> png判断")
                                FileInputStream("upload/legal_id_card/${uuid}_back.png")
                            } catch (e: Exception) {
                                log.debug("\t> NotFounded")
                                ClassPathResource("images/no-image.png").file.inputStream()
                            }
                        }
                    }
                } else {
                    log.debug("\t> 自定义图片")
                    try {
                        FileInputStream("upload/legal_id_card/${uuid}")
                    } catch (e: Exception) {
                        ClassPathResource("images/no-image.png").file.inputStream()
                    }
                }
            log.info("============================================================")
            this.writeImageToResponse(getImage, response)
        }
    }

    /**
     * writeImageToResponse
     *
     * 将图片写入响应
     *
     * @param getImage FileInputStream
     * @param response HttpServletResponse
     */
    private fun writeImageToResponse(getImage: FileInputStream, response: HttpServletResponse) {
        response.contentType = "image/jpeg"
        response.outputStream.apply {
            write(getImage.readAllBytes())
            flush()
            close()
        }
    }
}