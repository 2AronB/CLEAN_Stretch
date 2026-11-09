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
        Source("S9", "Afonso et al. 2021", "Afonso J, et al. Strength training versus stretching for improving range of motion. Healthcare. 2021;9:427.", "https://doi.org/10.3390/healthcare9040427"),
        Source("S10", "O'Sullivan et al. 2012", "O'Sullivan K, et al. The effects of eccentric training on lower limb flexibility. Br J Sports Med. 2012;46:838–845.", "https://bjsm.bmj.com/content/46/12/838"),
        Source("S19", "Louw et al. 2017", "Louw S, et al. Exercise in office workers with neck pain. S Afr J Physiother. 2017;73:392.", "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC6093121/"),
        Source("S30", "Ehlers-Danlos Society", "The Ehlers-Danlos Society. Physical therapy.", "https://www.ehlers-danlos.com/physical-therapy/"),
        Source("S34", "Skinner et al. 2020", "Skinner B, et al. Foam rolling on range of motion, recovery and athletic performance: systematic review and meta-analysis. J Bodyw Mov Ther. 2020;24:105–122.", "https://pubmed.ncbi.nlm.nih.gov/32825976"),
        Source("S36", "Behm et al. 2020", "Behm DG, et al. Foam rolling prescription: a clinical commentary. J Strength Cond Res. 2020;34:3301–3308.", "https://paulogentil.com/pdf/Foam%20Rolling%20Prescription%20-%20A%20Clinical%20Commentary.pdf"),
        Source("S44", "Bartsch et al. 2021", "Bartsch KM, et al. Expert consensus on the contraindications and cautions of foam rolling: international Delphi study. J Clin Med. 2021;10:5360.", "https://www.pubmed.gov/34830642/"),
        Source("CREDIT-VOICE", "Voice: Kokoro", "Spoken cues use the af_heart voice of Kokoro-82M by hexgrad, licensed under the Apache License 2.0. Clips are made in advance and play offline.", "https://huggingface.co/hexgrad/Kokoro-82M"),
    )

    fun byId(id: String): Source? = all.firstOrNull { it.id == id }
}
