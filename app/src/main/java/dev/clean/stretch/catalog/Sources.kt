package dev.clean.stretch.catalog

/** Offline citations shown on the Sources screen. IDs match docs/EVIDENCE.md. */
object Sources {
    val all: List<Source> = listOf(
        Source("S1", "Thomas et al. 2018", "Thomas E, Bianco A, Paoli A, Palma A. The relation between stretching typology and stretching duration: the effects on range of motion. Int J Sports Med. 2018;39:243–254.", "https://pubmed.ncbi.nlm.nih.gov/29506306/"),
        Source("S2", "Ingram et al. 2024", "Ingram LA, Tomkinson GR, d'Unienville NMA, et al. Optimising the dose of static stretching to improve flexibility: a systematic review, meta-analysis and multivariate meta-regression. Sports Med. 2025;55:597–617 (published online 2024).","https://pubmed.ncbi.nlm.nih.gov/39614059/"),
        Source("S3", "Warneke et al. 2025", "Warneke K, Thomas E, Blazevich AJ, et al. Practical recommendations on stretching exercise: a Delphi consensus statement of international research experts. J Sport Health Sci. 2025;14:101067.", "https://doi.org/10.1016/j.jshs.2025.101067"),
        Source("S12", "Decoster et al. 2005", "Decoster LC, Cleland J, Altieri C, Russell P. The effects of hamstring stretching on range of motion: a systematic literature review. J Orthop Sports Phys Ther. 2005;35:377–387.", "https://pubmed.ncbi.nlm.nih.gov/16001909/"),
        Source("S13", "Decoster et al. 2004", "Decoster LC, Scanlon RL, Horn KD, Cleland J. Standing and supine hamstring stretching are equally effective. J Athl Train. 2004;39:330–334.", "https://pmc.ncbi.nlm.nih.gov/articles/PMC535525"),
        Source("S15", "ACSM 2011", "Garber CE, et al. American College of Sports Medicine position stand: quantity and quality of exercise. Med Sci Sports Exerc. 2011;43:1334–1359.", "https://www.acsm.org/blog-detail/acsm-certified-blog/2021/03/18/stretching-and-flexibility-guidelines-update"),
        Source("CREDIT-VOICE", "Voice: Kokoro", "Spoken cues use the af_heart voice of Kokoro-82M by hexgrad, licensed under the Apache License 2.0. Clips are made in advance and play offline.", "https://huggingface.co/hexgrad/Kokoro-82M"),
    )

    fun byId(id: String): Source? = all.firstOrNull { it.id == id }
}
