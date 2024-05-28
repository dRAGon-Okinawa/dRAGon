import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Easy to Use',
    description: (
      <>
        dRAGon was designed from the ground up to be easily installed and
        used to get your RAG pipeline up and running quickly.
      </>
    ),
  },
  {
    title: 'Focus on What Matters',
    description: (
      <>
        dRAGon lets you focus on your RAG pipeline, and we&apos;ll do the chores. Go
        ahead and import your docs into the pipeline and let dRAGon do the rest.
      </>
    ),
  },
  {
    title: 'Powered by Spring Boot and Vue.js',
    description: (
      <>
        dRAGon is built on top of Spring Boot and Vue.js, two of the most popular
        open-source frameworks for building strong backends and frontends.
      </>
    ),
  },
];

function Feature({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        Image here
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
