/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.search.elasticsearch6.internal.aggregation;

import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.AggregationResultTranslator;
import com.liferay.portal.search.aggregation.AggregationResults;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.BucketAggregationResult;
import com.liferay.portal.search.aggregation.bucket.ChildrenAggregation;
import com.liferay.portal.search.aggregation.bucket.ChildrenAggregationResult;
import com.liferay.portal.search.aggregation.bucket.DateHistogramAggregation;
import com.liferay.portal.search.aggregation.bucket.DateHistogramAggregationResult;
import com.liferay.portal.search.aggregation.bucket.DateRangeAggregation;
import com.liferay.portal.search.aggregation.bucket.DiversifiedSamplerAggregation;
import com.liferay.portal.search.aggregation.bucket.DiversifiedSamplerAggregationResult;
import com.liferay.portal.search.aggregation.bucket.GeoHashGridAggregation;
import com.liferay.portal.search.aggregation.bucket.GeoHashGridAggregationResult;
import com.liferay.portal.search.aggregation.bucket.GlobalAggregation;
import com.liferay.portal.search.aggregation.bucket.GlobalAggregationResult;
import com.liferay.portal.search.aggregation.bucket.HistogramAggregation;
import com.liferay.portal.search.aggregation.bucket.HistogramAggregationResult;
import com.liferay.portal.search.aggregation.bucket.MissingAggregation;
import com.liferay.portal.search.aggregation.bucket.MissingAggregationResult;
import com.liferay.portal.search.aggregation.bucket.NestedAggregation;
import com.liferay.portal.search.aggregation.bucket.NestedAggregationResult;
import com.liferay.portal.search.aggregation.bucket.RangeAggregation;
import com.liferay.portal.search.aggregation.bucket.RangeAggregationResult;
import com.liferay.portal.search.aggregation.bucket.ReverseNestedAggregation;
import com.liferay.portal.search.aggregation.bucket.ReverseNestedAggregationResult;
import com.liferay.portal.search.aggregation.bucket.SamplerAggregation;
import com.liferay.portal.search.aggregation.bucket.SamplerAggregationResult;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.AvgAggregation;
import com.liferay.portal.search.aggregation.metrics.AvgAggregationResult;
import com.liferay.portal.search.aggregation.metrics.CardinalityAggregation;
import com.liferay.portal.search.aggregation.metrics.CardinalityAggregationResult;
import com.liferay.portal.search.aggregation.metrics.ExtendedStatsAggregation;
import com.liferay.portal.search.aggregation.metrics.ExtendedStatsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.MaxAggregation;
import com.liferay.portal.search.aggregation.metrics.MaxAggregationResult;
import com.liferay.portal.search.aggregation.metrics.MinAggregation;
import com.liferay.portal.search.aggregation.metrics.MinAggregationResult;
import com.liferay.portal.search.aggregation.metrics.PercentileRanksAggregation;
import com.liferay.portal.search.aggregation.metrics.PercentileRanksAggregationResult;
import com.liferay.portal.search.aggregation.metrics.PercentilesAggregation;
import com.liferay.portal.search.aggregation.metrics.PercentilesAggregationResult;
import com.liferay.portal.search.aggregation.metrics.StatsAggregation;
import com.liferay.portal.search.aggregation.metrics.StatsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.SumAggregation;
import com.liferay.portal.search.aggregation.metrics.SumAggregationResult;
import com.liferay.portal.search.aggregation.metrics.ValueCountAggregation;
import com.liferay.portal.search.aggregation.metrics.ValueCountAggregationResult;
import com.liferay.portal.search.aggregation.pipeline.PercentilesBucketPipelineAggregationResult;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationResultTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.pipeline.ElasticsearchPipelineAggregationResultTranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.elasticsearch.join.aggregations.Children;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.missing.Missing;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNested;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.sampler.Sampler;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentileRanks;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;

/**
 * @author Michael C. Han
 */
public class ElasticsearchAggregationResultTranslator
	implements AggregationResultTranslator, AggregationResultTranslatorFactory,
			   PipelineAggregationResultTranslatorFactory {

	public ElasticsearchAggregationResultTranslator(
		org.elasticsearch.search.aggregations.Aggregation
			elasticsearchAggregation,
		AggregationResults aggregationResults) {

		_elasticsearchAggregation = elasticsearchAggregation;
		_aggregationResults = aggregationResults;
	}

	@Override
	public AggregationResultTranslator createAggregationResultTranslator(
		org.elasticsearch.search.aggregations.Aggregation
			elasticsearchAggregation) {

		return new ElasticsearchAggregationResultTranslator(
			elasticsearchAggregation, _aggregationResults);
	}

	@Override
	public PipelineAggregationResultTranslator
		createPipelineAggregationResultTranslator(
			org.elasticsearch.search.aggregations.Aggregation
				elasticsearchAggregation) {

		return new ElasticsearchPipelineAggregationResultTranslator(
			elasticsearchAggregation, _aggregationResults);
	}

	@Override
	public AvgAggregationResult visit(AvgAggregation avgAggregation) {
		Avg avg = (Avg)_elasticsearchAggregation;

		return _aggregationResults.avg(avg.getName(), avg.getValue());
	}

	@Override
	public CardinalityAggregationResult visit(
		CardinalityAggregation cardinalityAggregation) {

		Cardinality cardinality = (Cardinality)_elasticsearchAggregation;

		return _aggregationResults.cardinality(
			cardinality.getName(), cardinality.getValue());
	}

	@Override
	public ChildrenAggregationResult visit(
		ChildrenAggregation childrenAggregation) {

		Children children = (Children)_elasticsearchAggregation;

		ChildrenAggregationResult childrenAggregationResult =
			_aggregationResults.children(
				children.getName(), children.getDocCount());

		childrenAggregationResult.addChildrenAggregationResults(
			translateAggregationResults(
				children.getAggregations(), childrenAggregation));

		return childrenAggregationResult;
	}

	@Override
	public DateHistogramAggregationResult visit(
		DateHistogramAggregation dateHistogramAggregation) {

		return translateBuckets(
			(Histogram)_elasticsearchAggregation,
			_aggregationResults.dateHistogram(
				_elasticsearchAggregation.getName()),
			dateHistogramAggregation);
	}

	@Override
	public RangeAggregationResult visit(
		DateRangeAggregation dateRangeAggregation) {

		return translateBuckets(
			(Range)_elasticsearchAggregation,
			_aggregationResults.range(_elasticsearchAggregation.getName()),
			dateRangeAggregation);
	}

	@Override
	public DiversifiedSamplerAggregationResult visit(
		DiversifiedSamplerAggregation diversifiedSamplerAggregation) {

		Sampler sampler = (Sampler)_elasticsearchAggregation;

		DiversifiedSamplerAggregationResult
			diversifiedSamplerAggregationResult =
				_aggregationResults.diversifiedSampler(
					sampler.getName(), sampler.getDocCount());

		diversifiedSamplerAggregationResult.addChildrenAggregationResults(
			translateAggregationResults(
				sampler.getAggregations(), diversifiedSamplerAggregation));

		return diversifiedSamplerAggregationResult;
	}

	@Override
	public ExtendedStatsAggregationResult visit(
		ExtendedStatsAggregation extendedStatsAggregation) {

		ExtendedStats extendedStats = (ExtendedStats)_elasticsearchAggregation;

		return _aggregationResults.extendedStats(
			extendedStats.getName(), extendedStats.getAvg(),
			extendedStats.getCount(), extendedStats.getMin(),
			extendedStats.getMax(), extendedStats.getSum(),
			extendedStats.getSumOfSquares(), extendedStats.getVariance(),
			extendedStats.getStdDeviation());
	}

	@Override
	public GeoHashGridAggregationResult visit(
		GeoHashGridAggregation geoHashGridAggregation) {

		GeoHashGrid geoHashGrid = (GeoHashGrid)_elasticsearchAggregation;

		return translateBuckets(
			geoHashGrid, _aggregationResults.geoHashGrid(geoHashGrid.getName()),
			geoHashGridAggregation);
	}

	@Override
	public GlobalAggregationResult visit(GlobalAggregation globalAggregation) {
		Global global = (Global)_elasticsearchAggregation;

		GlobalAggregationResult globalAggregationResult =
			_aggregationResults.global(global.getName(), global.getDocCount());

		globalAggregationResult.addChildrenAggregationResults(
			translateAggregationResults(
				global.getAggregations(), globalAggregation));

		return globalAggregationResult;
	}

	@Override
	public HistogramAggregationResult visit(
		HistogramAggregation histogramAggregation) {

		return translateBuckets(
			(Histogram)_elasticsearchAggregation,
			_aggregationResults.histogram(_elasticsearchAggregation.getName()),
			histogramAggregation);
	}

	@Override
	public MaxAggregationResult visit(MaxAggregation maxAggregation) {
		Max max = (Max)_elasticsearchAggregation;

		return _aggregationResults.max(max.getName(), max.getValue());
	}

	@Override
	public MinAggregationResult visit(MinAggregation minAggregation) {
		Min min = (Min)_elasticsearchAggregation;

		return _aggregationResults.min(min.getName(), min.getValue());
	}

	@Override
	public MissingAggregationResult visit(
		MissingAggregation missingAggregation) {

		Missing missing = (Missing)_elasticsearchAggregation;

		MissingAggregationResult missingAggregationResult =
			_aggregationResults.missing(
				missing.getName(), missing.getDocCount());

		missingAggregationResult.addChildrenAggregationResults(
			translateAggregationResults(
				missing.getAggregations(), missingAggregation));

		return missingAggregationResult;
	}

	@Override
	public NestedAggregationResult visit(NestedAggregation nestedAggregation) {
		Nested nested = (Nested)_elasticsearchAggregation;

		NestedAggregationResult nestedAggregationResult =
			_aggregationResults.nested(nested.getName(), nested.getDocCount());

		List<AggregationResult> aggregationResults =
			translateAggregationResults(
				nested.getAggregations(), nestedAggregation);

		nestedAggregationResult.addChildrenAggregationResults(
			aggregationResults);

		return nestedAggregationResult;
	}

	@Override
	public PercentileRanksAggregationResult visit(
		PercentileRanksAggregation percentileRanksAggregation) {

		PercentileRanks percentileRanks =
			(PercentileRanks)_elasticsearchAggregation;

		PercentileRanksAggregationResult percentileRanksAggregationResult =
			_aggregationResults.percentileRanks(percentileRanks.getName());

		percentileRanks.forEach(
			percentileRank ->
				percentileRanksAggregationResult.addPercentile(
					percentileRank.getValue(), percentileRank.getPercent()));

		return percentileRanksAggregationResult;
	}

	@Override
	public PercentilesAggregationResult visit(
		PercentilesAggregation percentilesAggregation) {

		Percentiles percentiles = (Percentiles)_elasticsearchAggregation;

		PercentilesBucketPipelineAggregationResult
			percentilesBucketPipelineAggregationResult =
				_aggregationResults.percentiles(percentiles.getName());

		percentiles.forEach(
			percentile ->
				percentilesBucketPipelineAggregationResult.addPercentile(
					percentile.getPercent(), percentile.getValue()));

		return percentilesBucketPipelineAggregationResult;
	}

	@Override
	public RangeAggregationResult visit(RangeAggregation rangeAggregation) {
		return translateBuckets(
			(Range)_elasticsearchAggregation,
			_aggregationResults.range(_elasticsearchAggregation.getName()),
			rangeAggregation);
	}

	@Override
	public ReverseNestedAggregationResult visit(
		ReverseNestedAggregation reverseNestedAggregation) {

		ReverseNested reverseNested = (ReverseNested)_elasticsearchAggregation;

		ReverseNestedAggregationResult reverseNestedAggregationResult =
			_aggregationResults.reverseNested(
				reverseNested.getName(), reverseNested.getDocCount());

		Aggregations aggregations = reverseNested.getAggregations();

		reverseNestedAggregationResult.addChildrenAggregationResults(
			translateAggregationResults(
				aggregations, reverseNestedAggregation));

		return reverseNestedAggregationResult;
	}

	@Override
	public SamplerAggregationResult visit(
		SamplerAggregation samplerAggregation) {

		Sampler sampler = (Sampler)_elasticsearchAggregation;

		SamplerAggregationResult samplerAggregationResult =
			_aggregationResults.sampler(
				sampler.getName(), sampler.getDocCount());

		samplerAggregationResult.addChildrenAggregationResults(
			translateAggregationResults(
				sampler.getAggregations(), samplerAggregation));

		return samplerAggregationResult;
	}

	@Override
	public StatsAggregationResult visit(StatsAggregation statsAggregation) {
		Stats stats = (Stats)_elasticsearchAggregation;

		return _aggregationResults.stats(
			stats.getName(), stats.getAvg(), stats.getCount(), stats.getMin(),
			stats.getMax(), stats.getSum());
	}

	@Override
	public SumAggregationResult visit(SumAggregation sumAggregation) {
		Sum sum = (Sum)_elasticsearchAggregation;

		return _aggregationResults.sum(sum.getName(), sum.getValue());
	}

	@Override
	public TermsAggregationResult visit(TermsAggregation termsAggregation) {
		Terms terms = (Terms)_elasticsearchAggregation;

		return translateBuckets(
			terms,
			_aggregationResults.terms(
				terms.getName(), terms.getDocCountError(),
				terms.getSumOfOtherDocCounts()),
			termsAggregation);
	}

	@Override
	public ValueCountAggregationResult visit(
		ValueCountAggregation valueCountAggregation) {

		ValueCount valueCount = (ValueCount)_elasticsearchAggregation;

		return _aggregationResults.valueCount(
			valueCount.getName(), valueCount.getValue());
	}

	protected Stream<AggregationResult> translate(
		Aggregations elasticsearchAggregations, Aggregation aggregation) {

		ElasticsearchAggregationResultsTranslator
			elasticsearchAggregationResultsTranslator =
				new ElasticsearchAggregationResultsTranslator(
					this, this, aggregation::getChildAggregation,
					aggregation::getPipelineAggregation);

		return elasticsearchAggregationResultsTranslator.translate(
			elasticsearchAggregations);
	}

	protected List<AggregationResult> translateAggregationResults(
		Aggregations elasticsearchAggregations, Aggregation aggregation) {

		List<AggregationResult> aggregationResults = new ArrayList<>();

		Stream<AggregationResult> stream = translate(
			elasticsearchAggregations, aggregation);

		stream.forEach(aggregationResults::add);

		return aggregationResults;
	}

	protected <T extends BucketAggregationResult> T translateBuckets(
		MultiBucketsAggregation multiBucketsAggregation,
		T bucketAggregationResult, Aggregation aggregation) {

		List<? extends MultiBucketsAggregation.Bucket>
			multiBucketAggregationBuckets =
				multiBucketsAggregation.getBuckets();

		multiBucketAggregationBuckets.forEach(
			multiBucketAggregationBucket -> {
				Bucket bucket = bucketAggregationResult.addBucket(
					multiBucketAggregationBucket.getKeyAsString(),
					multiBucketAggregationBucket.getDocCount());

				Stream<AggregationResult> stream = translate(
					multiBucketAggregationBucket.getAggregations(),
					aggregation);

				stream.forEach(bucket::addChildAggregationResult);
			});

		return bucketAggregationResult;
	}

	private final AggregationResults _aggregationResults;
	private final org.elasticsearch.search.aggregations.Aggregation
		_elasticsearchAggregation;

}